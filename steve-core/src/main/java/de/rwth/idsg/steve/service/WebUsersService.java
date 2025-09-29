/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.WebUserRepository;
import de.rwth.idsg.steve.repository.dto.WebUser;
import de.rwth.idsg.steve.service.dto.WebUserOverview;
import de.rwth.idsg.steve.web.dto.WebUserAuthority;
import de.rwth.idsg.steve.web.dto.WebUserBaseForm;
import de.rwth.idsg.steve.web.dto.WebUserForm;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.security.core.context.SecurityContextHolder.getContextHolderStrategy;

/**
 * Inspired by {@link org.springframework.security.provisioning.JdbcUserDetailsManager}
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2024
 */
@Service
@RequiredArgsConstructor
public class WebUsersService implements UserDetailsManager {

    // Because Guava's cache does not accept a null value
    private static final UserDetails DUMMY_USER = new User("#", "#", Collections.emptyList());

    private final SteveProperties steveProperties;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;
    private final WebUserRepository webUserRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = getContextHolderStrategy();

    private final Cache<String, UserDetails> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // TTL
            .maximumSize(100)
            .build();

    @EventListener
    public void afterStart(ContextRefreshedEvent event) {
        if (this.hasUserWithAuthority("ADMIN")) {
            return;
        }

        var headerVal = steveProperties.getWebApi().getHeaderValue();
        var encodedApiPassword = (headerVal == null || headerVal.isBlank()) ? null : passwordEncoder.encode(headerVal);

        var user = WebUser.builder()
                .login(steveProperties.getAuth().getUsername())
                .password(passwordEncoder.encode(steveProperties.getAuth().getPassword()))
                .apiPassword(encodedApiPassword)
                .enabled(true)
                .authorities(EnumSet.of(WebUserAuthority.ADMIN))
                .build();

        webUserRepository.createUser(user);
    }

    @Override
    public void createUser(UserDetails user) {
        validateUserDetails(user);
        var webUser = toWebUser(user);
        webUserRepository.createUser(webUser);
        userCache.invalidate(user.getUsername());
    }

    @Override
    public void updateUser(UserDetails user) {
        validateUserDetails(user);
        var webUser = toWebUser(user);
        webUserRepository.updateUser(webUser);
        userCache.invalidate(user.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        webUserRepository.deleteUser(username);
        userCache.invalidate(username);
    }

    /**
     * Not only just an SQL Update.
     * The flow is inspired by {@link JdbcUserDetailsManager#changePassword(String, String)}
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        var currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context for current user.");
        }

        var username = currentUser.getName();
        webUserRepository.changePassword(username, passwordEncoder.encode(newPassword));

        var authentication = createNewAuthentication(currentUser);
        var context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }

    @Override
    public boolean userExists(String username) {
        return webUserRepository.userExists(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var webUser = webUserRepository.loadUserByUsername(username).orElse(null);

        if (webUser == null) {
            throw new UsernameNotFoundException(username);
        }

        return User.withUsername(webUser.getLogin())
                .password(webUser.getPassword())
                .disabled(!webUser.isEnabled())
                .authorities(webUser.getAuthorities().stream()
                        .map(WebUserAuthority::getValues)
                        .flatMap(Set::stream)
                        .toArray(String[]::new))
                .build();
    }

    public UserDetails loadUserByUsernameForApi(String username) {
        try {
            var userExt = userCache.get(username, () -> {
                var user = this.loadUserByUsernameForApiInternal(username);
                // map null to dummy
                return (user == null) ? DUMMY_USER : user;
            });
            // map dummy back to null
            return (userExt == DUMMY_USER) ? null : userExt;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(int webUserPk) {
        var existing = webUserRepository.loadUserByUserPk(webUserPk).orElse(null);
        webUserRepository.deleteUser(webUserPk);
        if (existing != null) {
            userCache.invalidate(existing.getLogin());
        }
    }

    public void changeStatusOfUser(String username, boolean enabled) {
        webUserRepository.changeStatusOfUser(username, enabled);
        userCache.invalidate(username);
    }

    public boolean hasUserWithAuthority(String authority) {
        var count = webUserRepository.getUserCountWithAuthority(authority);
        return count > 0;
    }

    // Methods for the website
    public void add(WebUserForm form) {
        createUser(toUserDetails(form));
    }

    public void update(WebUserBaseForm form) {
        validateUserDetails(toUserDetailsBaseForm(form));
        var webUser = WebUser.builder()
                .webUserPk(form.getWebUserPk())
                .login(form.getWebUsername())
                .enabled(form.getEnabled())
                .authorities(Set.of(form.getAuthorities()))
                .build();
        webUserRepository.updateUserByPk(webUser);
        userCache.invalidate(form.getWebUsername());
    }

    public void updatePassword(WebUserForm form) {
        webUserRepository.changePassword(form.getWebUserPk(), passwordEncoder.encode(form.getPassword()));
    }

    public void updateApiPassword(WebUserForm form) {
        String newPassword = null;
        if (form.getApiPassword() != null && !form.getApiPassword().isEmpty()) {
            newPassword = passwordEncoder.encode(form.getApiPassword());
        }
        webUserRepository.changeApiPassword(form.getWebUserPk(), newPassword);
    }

    public List<WebUserOverview> getOverview(WebUserQueryForm form) {
        return webUserRepository.getOverview(form);
    }

    public WebUserBaseForm getDetails(Integer webUserPk) {
        var ur = webUserRepository
                .loadUserByUserPk(webUserPk)
                .orElseThrow(() -> new SteveException.NotFound("There is no user with id '%d'".formatted(webUserPk)));

        return getWebUserBaseForm(ur);
    }

    public WebUserBaseForm getDetails(String webUserName) {
        var ur = webUserRepository
                .loadUserByUsername(webUserName)
                .orElseThrow(() ->
                        new SteveException.NotFound("There is no user with username '%s'".formatted(webUserName)));

        return getWebUserBaseForm(ur);
    }

    private static WebUserBaseForm getWebUserBaseForm(WebUser ur) {
        var form = new WebUserBaseForm();
        form.setWebUserPk(ur.getWebUserPk());
        form.setEnabled(ur.isEnabled());
        form.setWebUsername(ur.getLogin());
        form.setAuthorities(WebUserAuthority.fromAuthorities(
                ur.getAuthorities().stream().map(WebUserAuthority::name).collect(Collectors.toSet())));
        return form;
    }

    // Helpers
    private UserDetails loadUserByUsernameForApiInternal(String username) {
        var webUser = webUserRepository.loadUserByUsername(username).orElse(null);
        if (webUser == null) {
            return null;
        }

        // the builder User.password(..) does not allow null values
        var apiPassword = webUser.getApiPassword();
        if (apiPassword == null) {
            apiPassword = "";
        }

        return User.withUsername(webUser.getLogin())
                .password(apiPassword)
                .disabled(!webUser.isEnabled())
                .authorities(webUser.getAuthorities().stream()
                        .map(WebUserAuthority::name)
                        .toArray(String[]::new))
                .build();
    }

    private WebUser toWebUser(UserDetails user) {
        var authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .map(WebUserAuthority::valueOf)
                .collect(Collectors.toSet());

        return WebUser.builder()
                .login(user.getUsername())
                .password(user.getPassword())
                .enabled(user.isEnabled())
                .authorities(authorities)
                .build();
    }

    private UserDetails toUserDetailsBaseForm(WebUserBaseForm form) {
        return User.withUsername(form.getWebUsername())
                .password("")
                .disabled(!form.getEnabled())
                .authorities(form.getAuthoritiesAsStrings().toArray(new String[0]))
                .build();
    }

    private UserDetails toUserDetails(WebUserForm form) {
        var rawPassword = form.getPassword();
        var encPw = "";
        if (rawPassword != null) {
            Assert.hasText(rawPassword, "Password may not be empty");
            encPw = passwordEncoder.encode(rawPassword);
        }
        return User.withUsername(form.getWebUsername())
                .password(encPw)
                .disabled(!form.getEnabled())
                .authorities(form.getAuthoritiesAsStrings().toArray(new String[0]))
                .build();
    }

    private String[] fromJson(String jsonArray) {
        try {
            return mapper.readValue(jsonArray, String[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toJson(Collection<? extends GrantedAuthority> authorities) {
        var auths = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .sorted() // keep a stable order of entries
                .collect(Collectors.toCollection(LinkedHashSet::new)); // prevent duplicates

        try {
            return mapper.writeValueAsString(auths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lifted from {@link JdbcUserDetailsManager#validateUserDetails(UserDetails)}
     */
    private static void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    /**
     * Lifted from {@link JdbcUserDetailsManager#validateAuthorities(Collection)}
     */
    private static void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");
        for (var authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }

    /**
     * Lifted from {@link JdbcUserDetailsManager#createNewAuthentication(Authentication, String)}
     */
    private Authentication createNewAuthentication(Authentication currentAuth) {
        var user = this.loadUserByUsername(currentAuth.getName());
        var newAuthentication = authenticated(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }
}

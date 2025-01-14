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
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.repository.WebUserRepository;
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.JSON;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
public class WebUserService implements UserDetailsManager {

    // Because Guava's cache does not accept a null value
    private static final UserDetails DUMMY_USER = new User("#", "#", Collections.emptyList());

    private final ObjectMapper jacksonObjectMapper;
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

        var headerVal = SteveConfiguration.CONFIG.getWebApi().getHeaderValue();

        var encodedApiPassword = StringUtils.isEmpty(headerVal)
            ? null
            : SteveConfiguration.CONFIG.getAuth().getPasswordEncoder().encode(headerVal);

        var user = new WebUserRecord()
            .setUsername(SteveConfiguration.CONFIG.getAuth().getUserName())
            .setPassword(SteveConfiguration.CONFIG.getAuth().getEncodedPassword())
            .setApiPassword(encodedApiPassword)
            .setEnabled(true)
            .setAuthorities(toJson(AuthorityUtils.createAuthorityList("ADMIN")));

        webUserRepository.createUser(user);
    }

    @Override
    public void createUser(UserDetails user) {
        validateUserDetails(user);
        var record = toWebUserRecord(user);
        webUserRepository.createUser(record);
    }

    @Override
    public void updateUser(UserDetails user) {
        validateUserDetails(user);
        var record = toWebUserRecord(user);
        webUserRepository.updateUser(record);
    }

    @Override
    public void deleteUser(String username) {
        webUserRepository.deleteUser(username);
    }

    /**
     * Not only just an SQL Update.
     * The flow is inspired by {@link JdbcUserDetailsManager#changePassword(String, String)}
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                "Can't change password as no Authentication object found in context for current user."
            );
        }

        String username = currentUser.getName();
        webUserRepository.changePassword(username, newPassword);

        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }

    @Override
    public boolean userExists(String username) {
        return webUserRepository.userExists(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUserRecord record = webUserRepository.loadUserByUsername(username);

        if (record == null) {
            throw new UsernameNotFoundException(username);
        }

        return User
            .withUsername(record.getUsername())
            .password(record.getPassword())
            .disabled(!record.getEnabled())
            .authorities(fromJson(record.getAuthorities()))
            .build();
    }

    public UserDetails loadUserByUsernameForApi(String username) {
        try {
            UserDetails userExt = userCache.get(username, () -> {
                UserDetails user = this.loadUserByUsernameForApiInternal(username);
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
        webUserRepository.deleteUser(webUserPk);
    }

    public void changeStatusOfUser(String username, boolean enabled) {
        webUserRepository.changeStatusOfUser(username, enabled);
    }

    public boolean hasUserWithAuthority(String authority) {
        Integer count = webUserRepository.getUserCountWithAuthority(authority);
        return count != null && count > 0;
    }

    private UserDetails loadUserByUsernameForApiInternal(String username) {
        WebUserRecord record = webUserRepository.loadUserByUsername(username);
        if (record == null) {
            return null;
        }

        // the builder User.password(..) does not allow null values
        String apiPassword = record.getApiPassword();
        if (apiPassword == null) {
            apiPassword = "";
        }

        return User
            .withUsername(record.getUsername())
            .password(apiPassword)
            .disabled(!record.getEnabled())
            .authorities(fromJson(record.getAuthorities()))
            .build();
    }

    private WebUserRecord toWebUserRecord(UserDetails user) {
        return new WebUserRecord()
            .setUsername(user.getUsername())
            .setPassword(user.getPassword())
            .setEnabled(user.isEnabled())
            .setAuthorities(toJson(user.getAuthorities()));
    }

    private String[] fromJson(JSON jsonArray) {
        try {
            return jacksonObjectMapper.readValue(jsonArray.data(), String[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JSON toJson(Collection<? extends GrantedAuthority> authorities) {
        Collection<String> auths = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .sorted() // keep a stable order of entries
            .collect(Collectors.toCollection(LinkedHashSet::new)); // prevent duplicates

        try {
            String str = jacksonObjectMapper.writeValueAsString(auths);
            return JSON.jsonOrNull(str);
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
        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }

    /**
     * Lifted from {@link JdbcUserDetailsManager#createNewAuthentication(Authentication, String)}
     */
    private Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        var user = this.loadUserByUsername(currentAuth.getName());
        var newAuthentication = authenticated(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }
}

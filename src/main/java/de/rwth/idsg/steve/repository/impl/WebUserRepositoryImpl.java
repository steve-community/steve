/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.repository.WebUserRepository;
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static jooq.steve.db.Tables.WEB_USER;
import static org.jooq.impl.DSL.condition;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.security.core.context.SecurityContextHolder.getContextHolderStrategy;

/**
 * Inspired by {@link org.springframework.security.provisioning.JdbcUserDetailsManager}
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.08.2024
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class WebUserRepositoryImpl implements WebUserRepository {

    private final DSLContext ctx;
    private final ObjectMapper jacksonObjectMapper;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = getContextHolderStrategy();

    @Override
    public void createUser(UserDetails user) {
        validateUserDetails(user);

        ctx.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, user.getUsername())
            .set(WEB_USER.PASSWORD, user.getPassword())
            .set(WEB_USER.ENABLED, user.isEnabled())
            .set(WEB_USER.AUTHORITIES, toJson(user.getAuthorities()))
            .execute();
    }

    @Override
    public void updateUser(UserDetails user) {
        validateUserDetails(user);

        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, user.getPassword())
            .set(WEB_USER.ENABLED, user.isEnabled())
            .set(WEB_USER.AUTHORITIES, toJson(user.getAuthorities()))
            .where(WEB_USER.USERNAME.eq(user.getUsername()))
            .execute();
    }

    @Override
    public void deleteUser(String username) {
        ctx.delete(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    @Override
    public void deleteUser(int webUserPk) {
        ctx.delete(WEB_USER)
            .where(WEB_USER.WEB_USER_PK.eq(webUserPk))
            .execute();
    }

    @Override
    public void changeStatusOfUser(String username, boolean enabled) {
        ctx.update(WEB_USER)
            .set(WEB_USER.ENABLED, enabled)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    @Override
    public boolean hasUserWithAuthority(String authority) {
        JSON authValue = JSON.json("\"" + authority + "\"");
        return ctx.selectOne()
            .from(WEB_USER)
            .where(condition("json_contains({0}, {1})", WEB_USER.AUTHORITIES, authValue))
            .fetchOptional()
            .isPresent();
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

        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, newPassword)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();

        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }

    @Override
    public boolean userExists(String username) {
        return ctx.selectOne()
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOptional()
            .isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUserRecord record = ctx.selectFrom(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne();

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
    private void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    /**
     * Lifted from {@link JdbcUserDetailsManager#validateAuthorities(Collection)}
     */
    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
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

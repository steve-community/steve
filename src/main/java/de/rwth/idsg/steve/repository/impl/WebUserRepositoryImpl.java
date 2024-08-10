package de.rwth.idsg.steve.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.repository.WebUserRepository;
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

import static jooq.steve.db.Tables.WEB_USER;

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
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

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
    public void changeStatusOfUser(String username, boolean enabled) {
        ctx.update(WEB_USER)
            .set(WEB_USER.ENABLED, enabled)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    /**
     * Not only just an SQL Update. The flow is inspired by {@link JdbcUserDetailsManager#changePassword(String, String)}
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context " + "for current user.");
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

    private String[] fromJson(String jsonArray) {
        try {
            return jacksonObjectMapper.readValue(jsonArray, String[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toJson(Collection<? extends GrantedAuthority> authorities) {
        List<String> auths = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        try {
            return jacksonObjectMapper.writeValueAsString(auths);
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
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

}

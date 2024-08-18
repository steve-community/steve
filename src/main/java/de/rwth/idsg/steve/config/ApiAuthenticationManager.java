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
package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.rwth.idsg.steve.service.WebUserService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.08.2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAuthenticationManager implements AuthenticationManager, AuthenticationEntryPoint {

    // Because Guava's cache does not accept a null value
    private static final UserDetails DUMMY_USER = new User("#", "#", Collections.emptyList());

    private final WebUserService webUserService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper jacksonObjectMapper;

    private final Cache<String, UserDetails> userCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES) // TTL
        .maximumSize(100)
        .build();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String apiPassword = (String) authentication.getCredentials();

        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(apiPassword)) {
            throw new BadCredentialsException("Required parameters missing");
        }

        UserDetails userDetails = getFromCacheOrDatabase(username);
        if (!areValuesSet(userDetails)) {
            throw new DisabledException("The user does not exist, exists but is disabled or has API access disabled.");
        }

        boolean match = passwordEncoder.matches(apiPassword, userDetails.getPassword());
        if (!match) {
            throw new BadCredentialsException("Invalid password");
        }

        return UsernamePasswordAuthenticationToken.authenticated(
            authentication.getPrincipal(),
            authentication.getCredentials(),
            userDetails.getAuthorities()
        );
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        var apiResponse = ApiControllerAdvice.createResponse(
            request.getRequestURL().toString(),
            status,
            authException.getMessage()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(jacksonObjectMapper.writeValueAsString(apiResponse));
    }

    private UserDetails getFromCacheOrDatabase(String username) {
        try {
            return userCache.get(username, () -> {
                UserDetails user = webUserService.loadUserByUsernameForApi(username);
                return (user == null) ? DUMMY_USER : user;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean areValuesSet(UserDetails userDetails) {
        if (userDetails == null || userDetails == DUMMY_USER) {
            return false;
        }
        if (!userDetails.isEnabled()) {
            return false;
        }
        if (Strings.isNullOrEmpty(userDetails.getPassword())) {
            return false;
        }
        return true;
    }

}

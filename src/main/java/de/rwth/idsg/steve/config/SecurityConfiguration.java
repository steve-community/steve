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
import de.rwth.idsg.steve.web.api.ApiControllerAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.01.2015
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Password encoding changed with spring-security 5.0.0. We either have to use a prefix before the password to
     * indicate which actual encoder {@link DelegatingPasswordEncoder} should use [1, 2] or specify the encoder as we do.
     *
     * [1] https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format
     * [2] {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return CONFIG.getAuth().getPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String prefix = CONFIG.getSpringManagerMapping();

        return http
            .authorizeHttpRequests(
                req -> req
                    .requestMatchers(
                        "/static/**",
                        CONFIG.getCxfMapping() + "/**",
                        WebSocketConfiguration.PATH_INFIX + "**",
                        "/WEB-INF/views/**" // https://github.com/spring-projects/spring-security/issues/13285#issuecomment-1579097065
                    ).permitAll()
                    .requestMatchers(prefix + "/**").hasAuthority("ADMIN")
            )
            // SOAP stations are making POST calls for communication. even though the following path is permitted for
            // all access, there is a global default behaviour from spring security: enable CSRF for all POSTs.
            // we need to disable CSRF for SOAP paths explicitly.
            .csrf(c -> c.ignoringRequestMatchers(CONFIG.getCxfMapping() + "/**"))
            .sessionManagement(
                req -> req.invalidSessionUrl(prefix + "/signin")
            )
            .formLogin(
                req -> req.loginPage(prefix + "/signin").permitAll()
            )
            .logout(
                req -> req.logoutUrl(prefix + "/signout")
            )
            .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeyFilterChain(HttpSecurity http, ObjectMapper jacksonObjectMapper) throws Exception {
        return http.securityMatcher(CONFIG.getApiMapping() + "/**")
            .csrf(k -> k.disable())
            .sessionManagement(k -> k.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilter(new ApiKeyFilter())
            .authorizeHttpRequests(k -> k.anyRequest().authenticated())
            .exceptionHandling(k -> k.authenticationEntryPoint(new ApiKeyAuthenticationEntryPoint(jacksonObjectMapper)))
            .build();
    }

    /**
     * Enable Web APIs only if both properties for API key are set. This has two consequences:
     * 1) Backwards compatibility: Existing installations with older properties file, that does not include these two
     * new keys, will not expose the APIs. Every call will be blocked by default.
     * 2) If you want to expose your APIs, you MUST set these properties. This action activates authentication (i.e.
     * APIs without authentication are not possible, and this is a good thing).
     */
    public static class ApiKeyFilter extends AbstractPreAuthenticatedProcessingFilter implements AuthenticationManager {

        private final String headerKey;
        private final String headerValue;
        private final boolean isApiEnabled;

        public ApiKeyFilter() {
            setAuthenticationManager(this);

            headerKey = CONFIG.getWebApi().getHeaderKey();
            headerValue = CONFIG.getWebApi().getHeaderValue();
            isApiEnabled = !Strings.isNullOrEmpty(headerKey) && !Strings.isNullOrEmpty(headerValue);

            if (!isApiEnabled) {
                log.warn("Web APIs will not be exposed. Reason: 'webapi.key' and 'webapi.value' are not set in config file");
            }
        }

        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            if (!isApiEnabled) {
                throw new DisabledException("Web APIs are not exposed");
            }
            return request.getHeader(headerKey);
        }

        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            return null;
        }

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            if (!isApiEnabled) {
                throw new DisabledException("Web APIs are not exposed");
            }

            String principal = (String) authentication.getPrincipal();
            authentication.setAuthenticated(headerValue.equals(principal));
            return authentication;
        }
    }

    public static class ApiKeyAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private final ObjectMapper mapper;

        private ApiKeyAuthenticationEntryPoint(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            HttpStatus status = HttpStatus.UNAUTHORIZED;

            var apiResponse = ApiControllerAdvice.createResponse(
                request.getRequestURL().toString(),
                status,
                "Full authentication is required to access this resource"
            );

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(mapper.writeValueAsString(apiResponse));
        }
    }
}

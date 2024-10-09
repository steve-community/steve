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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.RequestMatcher;

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

        RequestMatcher toOverview = (request) -> request.getParameter("backToOverview") != null;

        return http
            .authorizeHttpRequests(
                req -> req
                    .requestMatchers(
                        "/static/**",
                        CONFIG.getCxfMapping() + "/**",
                        WebSocketConfiguration.PATH_INFIX + "**",
                        "/WEB-INF/views/**" // https://github.com/spring-projects/spring-security/issues/13285#issuecomment-1579097065
                    ).permitAll()
                    .requestMatchers(prefix + "/home").hasAnyAuthority("USER", "ADMIN")
                    // webuser
                        //only allowed to change the own password
                    .requestMatchers(prefix + "/webusers" + "/password/{name}")
                        .access(new WebExpressionAuthorizationManager("#name == authentication.name"))
                        // otherwise denies access on backToOverview!
                    .requestMatchers(toOverview).hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, prefix + "/webusers/**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, prefix + "/webusers/**").hasAuthority("ADMIN")
                    // users
                    .requestMatchers(prefix + "/users").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/users" + "/details/**").hasAnyAuthority("USER", "ADMIN")
                     //ocppTags
                    .requestMatchers(prefix + "/ocppTags").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/ocppTags" + "/details/**").hasAnyAuthority("USER", "ADMIN")
                     // chargepoints
                    .requestMatchers(prefix + "/chargepoints").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/chargepoints" + "/details/**").hasAnyAuthority("USER", "ADMIN")
                     // transactions and reservations
                    .requestMatchers(prefix + "/transactions").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/transactions" + "/details/**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/reservations").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/reservations" + "/**").hasAnyAuthority("ADMIN")
                     // singout and noAccess
                    .requestMatchers(prefix + "/signout/" + "**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers(prefix + "/noAccess/" + "**").hasAnyAuthority("USER", "ADMIN")
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
            .exceptionHandling(
                req -> req.accessDeniedPage(prefix + "/noAccess")
            )
            .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeyFilterChain(HttpSecurity http, ApiAuthenticationManager apiAuthenticationManager) throws Exception {
        return http.securityMatcher(CONFIG.getApiMapping() + "/**")
            .csrf(k -> k.disable())
            .sessionManagement(k -> k.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilter(new BasicAuthenticationFilter(apiAuthenticationManager, apiAuthenticationManager))
            .authorizeHttpRequests(k -> k.anyRequest().authenticated())
            .build();
    }
}

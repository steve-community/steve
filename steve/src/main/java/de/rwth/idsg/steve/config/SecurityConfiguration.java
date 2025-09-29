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
package de.rwth.idsg.steve.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.DispatcherType;

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
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SteveProperties steveProperties)
            throws Exception {
        var prefix = steveProperties.getPaths().getManagerMapping();

        RequestMatcher toOverview = request -> {
            var param = request.getParameter("backToOverview");
            return param != null && !param.isEmpty();
        };

        return http.authorizeHttpRequests(req -> req.dispatcherTypeMatchers(
                                DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR)
                        .permitAll()
                        .requestMatchers(
                                "/", // we have RootRedirectController to redirect "/" to "/manager"
                                "/static/**",
                                steveProperties.getPaths().getSoapMapping() + "/**",
                                steveProperties.getPaths().getWebsocketMapping()
                                        + steveProperties.getPaths().getRouterEndpointPath() + "/**")
                        .permitAll()
                        // Permit Spring MVC error endpoint so exceptions can be shown
                        .requestMatchers("/error", "/error/**")
                        .permitAll()
                        // https://github.com/spring-projects/spring-security/issues/13285#issuecomment-1579097065
                        .requestMatchers("/WEB-INF/views/**")
                        .permitAll()
                        .requestMatchers(
                                prefix + "/signin", // GET login page and POST login processing
                                prefix + "/noAccess",
                                prefix + "/noAccess/**",
                                prefix + "/css/**",
                                prefix + "/js/**",
                                prefix + "/images/**")
                        .permitAll()
                        .requestMatchers(prefix + "/home")
                        .hasAnyAuthority("USER", "ADMIN")
                        // webuser
                        // only allowed to change the own password
                        .requestMatchers(prefix + "/webusers/password/{name}")
                        .access(new WebExpressionAuthorizationManager("#name == authentication.name"))
                        .requestMatchers(prefix + "/webusers/apipassword/{name}")
                        .access(new WebExpressionAuthorizationManager("#name == authentication.name"))
                        // otherwise denies access on backToOverview!
                        .requestMatchers(toOverview)
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, prefix + "/webusers/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, prefix + "/webusers/**")
                        .hasAuthority("ADMIN")
                        // users
                        .requestMatchers(prefix + "/users")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/users/details/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        // ocppTags
                        .requestMatchers(prefix + "/ocppTags")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/ocppTags/details/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        // chargepoints
                        .requestMatchers(prefix + "/chargepoints")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/chargepoints/details/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        // transactions and reservations
                        .requestMatchers(prefix + "/transactions")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/transactions/details/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/reservations")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/reservations/**")
                        .hasAnyAuthority("ADMIN")
                        // signout and noAccess
                        .requestMatchers(prefix + "/signout/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(prefix + "/noAccess", prefix + "/noAccess/**")
                        .permitAll()
                        .requestMatchers(prefix + "/**")
                        .hasAuthority("ADMIN"))
                // SOAP stations are making POST calls for communication. even though the following path is permitted
                // for
                // all access, there is a global default behaviour from spring security: enable CSRF for all POSTs.
                // we need to disable CSRF for SOAP paths explicitly.
                .csrf(c -> c.ignoringRequestMatchers(steveProperties.getPaths().getSoapMapping() + "/**"))
                .requestCache(c -> c.disable())
                .sessionManagement(req -> req.invalidSessionUrl(prefix + "/signin"))
                .formLogin(req -> req.loginPage(prefix + "/signin")
                        .loginProcessingUrl(prefix + "/signin")
                        .defaultSuccessUrl(prefix + "/home", true)
                        .failureUrl(prefix + "/signin?error")
                        .permitAll())
                .logout(req -> req.logoutUrl(prefix + "/signout")
                        .logoutSuccessUrl(prefix + "/signin?logout")
                        .permitAll())
                .exceptionHandling(req -> req.accessDeniedPage(prefix + "/noAccess"))
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeyFilterChain(
            HttpSecurity http, SteveProperties steveProperties, ApiAuthenticationManager apiAuthenticationManager)
            throws Exception {
        return http.securityMatcher(steveProperties.getPaths().getApiMapping() + "/**")
                .csrf(k -> k.disable())
                .sessionManagement(k -> k.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new BasicAuthenticationFilter(apiAuthenticationManager, apiAuthenticationManager))
                .authorizeHttpRequests(k -> k.anyRequest().authenticated())
                .build();
    }
}

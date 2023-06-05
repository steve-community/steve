/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariDataSource;
import de.rwth.idsg.steve.SteveProdCondition;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @author Frank Brosi
 * @since 07.01.2015
 */
@Slf4j
@Configuration
@EnableWebSecurity
@Conditional(SteveProdCondition.class)
public class SecurityConfiguration {

    @Autowired
    private HikariDataSource dataSource;

    
    /**
     * Password encoding changed with spring-security 5.0.0. We either have to use a prefix before the password to
     * indicate which actual encoder {@link DelegatingPasswordEncoder} should use [1, 2] or specify the encoder as we do.
     *
     * [1] https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format
     * [2] {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
    */

    /**
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return CONFIG.getAuth().getPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
            "/static/**",
            CONFIG.getCxfMapping() + "/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String prefix = CONFIG.getSpringManagerMapping();
        return http
            .authorizeRequests(
                 req -> req
                    .antMatchers(prefix + "/home").hasAnyRole("USER", "ADMIN")
                    .antMatchers(prefix + "/users/" + "**").hasAnyRole("USER", "ADMIN")
                    .antMatchers(prefix + "/ocppTags/" + "**").hasAnyRole("USER", "ADMIN")
                    .antMatchers(prefix + "/signout/" + "**").hasAnyRole("USER", "ADMIN")
                    .antMatchers(prefix + "/noAccess/" + "**").hasAnyRole("USER", "ADMIN")
                    .antMatchers(prefix + "/**").hasRole("ADMIN")
                )
            .sessionManagement(
                 req -> req
                    .invalidSessionUrl(prefix + "/signin")
                )
            .formLogin(
                req -> req
                    .loginPage(prefix + "/signin")
                    .permitAll()
                )
            .logout(
                req -> req
                    .logoutUrl(prefix + "/signout")
                )

            .exceptionHandling(
                req -> req
                    .accessDeniedPage(prefix + "/noAccess")
                )
            .build();
    }

    /**
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
      throws Exception {
        auth.jdbcAuthentication()
            .passwordEncoder(CONFIG.getAuth().getPasswordEncoder())
            .dataSource(dataSource)
            .usersByUsernameQuery("select username,password,enabled from webusers where username = ?")
            .authoritiesByUsernameQuery("select username,authority from webauthorities where username = ?");
    }


    /**
     *
     * @return
     */
    @Bean
    public UserDetailsManager authenticateUsers() {
      JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
      // Adapt the SQL-Commands to the correct table names (user -> webuser; authorities -> webauthorities)
      users.setAuthoritiesByUsernameQuery("select username,authority from webauthorities where username=?");
      users.setUsersByUsernameQuery("select username,password,enabled from webusers where username=?");

      /* Adding the admin from config-file to the database/webusers
            --> changing the name in the file will not delete the corresponding webuser in the database!
      users.setCreateUserSql("insert into webusers (username, password, enabled) values (?,?,?)");
      users.setCreateAuthoritySql("insert into webauthorities (username, authority) values (?,?)");
      users.setUserExistsSql("select username from webusers where username = ?");
      users.setUpdateUserSql("update webusers set password = ?, enabled = ? where username = ?");
      users.setDeleteUserAuthoritiesSql("delete from webauthorities where username = ?");

      UserDetails localUser = User.builder()
                .username(CONFIG.getAuth().getUserName())
                .password(CONFIG.getAuth().getEncodedPassword())
                .roles("USER")
                .build();

      if (users.userExists(CONFIG.getAuth().getUserName()))
      {
          users.updateUser(localUser);
      }
      else
      {
          users.createUser(localUser);
      }
      */
      return users;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeyFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        return http.antMatcher(CONFIG.getApiMapping() + "/**")
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new ApiKeyFilter())
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(new ApiKeyAuthenticationEntryPoint(objectMapper))
            .and()
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

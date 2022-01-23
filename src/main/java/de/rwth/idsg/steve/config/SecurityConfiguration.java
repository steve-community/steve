/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.SteveProdCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.01.2015
 */
@Configuration
@EnableWebSecurity
@Conditional(SteveProdCondition.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Password encoding changed with spring-security 5.0.0. We either have to use a prefix before the password to
     * indicate which actual encoder {@link DelegatingPasswordEncoder} should use [1, 2] or specify the encoder as we do.
     *
     * [1] https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format
     * [2] {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .passwordEncoder(CONFIG.getAuth().getPasswordEncoder())
            .withUser(CONFIG.getAuth().getUserName())
            .password(CONFIG.getAuth().getEncodedPassword())
            .roles("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
           .antMatchers("/static/**")
           .antMatchers("/views/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String prefix = "/manager/";
        http
            .authorizeRequests()
                .antMatchers(prefix + "**").hasRole("ADMIN")
                .and()
            .sessionManagement()
                .invalidSessionUrl(prefix + "signin")
                .and()
            .formLogin()
                .loginPage(prefix + "signin")
                .permitAll()
                .and()
            .logout()
                .logoutUrl(prefix + "signout")
                .and()
            .httpBasic();
    }

}

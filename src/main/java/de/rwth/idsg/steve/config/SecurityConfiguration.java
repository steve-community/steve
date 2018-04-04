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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 07.01.2015
 */
@Configuration
@EnableWebSecurity
@Conditional(SteveProdCondition.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Password encoding changed with spring-security 5.0.0. We either have to use a prefix before the password to
     * indicate which actual encoder {@link DelegatingPasswordEncoder} should use [1, 2] or specify the encoder as we do.
     * {@link NoOpPasswordEncoder} is deprecated because it is not secure since it is not a one way hash function and
     * uses plain text matching, but is good enough for our simple user management and login authorization case.
     *
     * [1] https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format
     * [2] {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
            .withUser(CONFIG.getAuth().getUserName())
            .password(CONFIG.getAuth().getPassword())
            .roles("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
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

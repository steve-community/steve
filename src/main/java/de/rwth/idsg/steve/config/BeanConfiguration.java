package de.rwth.idsg.steve.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.web.GsonHttpMessageConverter;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Configuration
@EnableWebMvc
@ComponentScan("de.rwth.idsg.steve")
public class BeanConfiguration extends WebMvcConfigurerAdapter {

    private HikariDataSource dataSource;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
     */
    private void initDataSource() {

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(SteveConfiguration.DB.CLASS_NAME);
        config.setJdbcUrl(SteveConfiguration.DB.URL);
        config.setUsername(SteveConfiguration.DB.USERNAME);
        config.setPassword(SteveConfiguration.DB.PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);
    }

    @Bean
    @Qualifier("jooqConfig")
    public org.jooq.Configuration jooqConfig() {

        initDataSource();

        // Configuration for JOOQ
        return new DefaultConfiguration()
                .set(SteveConfiguration.DB.JOOQ_DIALECT)
                .set(new DataSourceConnectionProvider(dataSource))
                .set(new Settings().withExecuteLogging(SteveConfiguration.DB.JOOQ_EXEC_LOGGING));
    }

    @PreDestroy
    public void shutDown() {
        if (dataSource != null) dataSource.shutdown();
    }

    // -------------------------------------------------------------------------
    // Web config
    // -------------------------------------------------------------------------

    /**
     * Resolver for JSP views/templates. Controller classes process the requests
     * and forward to JSP files for rendering.
     */
    @Bean
    public UrlBasedViewResolver urlBasedViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/views/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    /**
     * Resource path for static content of the Web interface.
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("static/");
        registry.addResourceHandler("/views/**").addResourceLocations("views/");
    }

    /**
     * GsonHttpMessageConverter is needed to return plain JSON objects to AJAX calls.
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/signin").setViewName("signin");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
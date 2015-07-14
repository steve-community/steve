package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rwth.idsg.steve.SteveConfiguration;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan("de.rwth.idsg.steve")
public class BeanConfiguration extends WebMvcConfigurerAdapter {

    private HikariDataSource dataSource;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
     */
    private void initDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(SteveConfiguration.DB.URL);
        config.setUsername(SteveConfiguration.DB.USERNAME);
        config.setPassword(SteveConfiguration.DB.PASSWORD);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
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
                .set(SQLDialect.MYSQL)
                .set(new DataSourceConnectionProvider(dataSource))
                .set(new Settings().withExecuteLogging(false));
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(5);
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
     * MappingJackson2HttpMessageConverter is needed to return plain JSON objects to AJAX calls.
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(converter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/manager/signin").setViewName("signin");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
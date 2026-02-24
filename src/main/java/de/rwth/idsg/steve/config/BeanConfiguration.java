/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rwth.idsg.steve.service.DummyReleaseCheckService;
import de.rwth.idsg.steve.service.GithubReleaseCheckService;
import de.rwth.idsg.steve.service.ReleaseCheckService;
import de.rwth.idsg.steve.utils.InternetChecker;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import tools.jackson.datatype.joda.JodaModule;

import javax.sql.DataSource;

import java.time.Clock;

import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan("de.rwth.idsg.steve")
public class BeanConfiguration implements WebMvcConfigurer {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
     */
    @Bean
    public HikariDataSource dataSource(DataSourceProperties properties) {
        HikariConfig hc = new HikariConfig();

        // set standard params
        hc.setJdbcUrl(properties.getUrl());
        hc.setUsername(properties.getUsername());
        hc.setPassword(properties.getPassword());

        // set non-standard params
        hc.addDataSourceProperty(PropertyKey.cachePrepStmts.getKeyName(), true);
        hc.addDataSourceProperty(PropertyKey.useServerPrepStmts.getKeyName(), true);
        hc.addDataSourceProperty(PropertyKey.prepStmtCacheSize.getKeyName(), 250);
        hc.addDataSourceProperty(PropertyKey.prepStmtCacheSqlLimit.getKeyName(), 2048);
        hc.addDataSourceProperty(PropertyKey.characterEncoding.getKeyName(), "utf8");
        hc.addDataSourceProperty(PropertyKey.connectionTimeZone.getKeyName(), SteveProperties.TIME_ZONE_ID);
        hc.addDataSourceProperty(PropertyKey.useSSL.getKeyName(), true);

        // https://github.com/steve-community/steve/issues/736
        hc.setMaxLifetime(580_000);

        return new HikariDataSource(hc);
    }

    /**
     * Can we re-use DSLContext as a Spring bean (singleton)? Yes, the Spring tutorial of
     * Jooq also does it that way, but only if we do not change anything about the
     * config after the init (which we don't do anyways) and if the ConnectionProvider
     * does not store any shared state (we use DataSourceConnectionProvider of Jooq, so no problem).
     *
     * Some sources and discussion:
     * - http://www.jooq.org/doc/3.6/manual/getting-started/tutorials/jooq-with-spring/
     * - http://jooq-user.narkive.com/2fvuLodn/dslcontext-and-threads
     * - https://groups.google.com/forum/#!topic/jooq-user/VK7KQcjj3Co
     * - http://stackoverflow.com/questions/32848865/jooq-dslcontext-correct-autowiring-with-spring
     */
    @Bean
    public DSLContext dslContext(DataSource dataSource,
                                 SteveProperties steveProperties) {
        Settings settings = new Settings()
                // Normally, the records are "attached" to the Configuration that created (i.e. fetch/insert) them.
                // This means that they hold an internal reference to the same database connection that was used.
                // The idea behind this is to make CRUD easier for potential subsequent store/refresh/delete
                // operations. We do not use or need that.
                .withAttachRecords(false)
                // To log or not to log the sql queries, that is the question
                .withExecuteLogging(steveProperties.getJooq().isExecutiveLogging());

        // Configuration for JOOQ
        org.jooq.Configuration conf = new DefaultConfiguration()
                .set(SQLDialect.MYSQL)
                .set(new DataSourceConnectionProvider(dataSource))
                .set(settings);

        return DSL.using(conf);
    }

    /**
     * ThreadPoolTaskScheduler is both a TaskScheduler and a TaskExecutor
     * https://github.com/spring-projects/spring-boot/issues/20308
     */
    @Bean
    @Primary
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("SteVe-TaskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();

        return scheduler;
    }

    /**
     * There might be instances deployed in a local/closed network with no internet connection. In such situations,
     * it is unnecessary to try to access Github every time, even though the request will time out and result
     * report will be correct (that there is no new version). With DummyReleaseCheckService we bypass the intermediate
     * steps and return a "no new version" report immediately.
     */
    @Bean
    public ReleaseCheckService releaseCheckService(SteveProperties steveProperties) {
        if (InternetChecker.isInternetAvailable()) {
            return new GithubReleaseCheckService(steveProperties);
        } else {
            return new DummyReleaseCheckService();
        }
    }

    // -------------------------------------------------------------------------
    // Web config
    // -------------------------------------------------------------------------

    /**
     * Resolver for JSP views/templates. Controller classes process the requests
     * and forward to JSP files for rendering.
     */
    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        // FIXME: Remove this resolver after all pages are migrated to Thymeleaf. Keep JSP fallback until then.
        resolver.setOrder(2);
        return resolver;
    }

    /**
     * Resource path for static content of the Web interface.
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("static/");
    }

    // -------------------------------------------------------------------------
    // API config
    // -------------------------------------------------------------------------

    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // default is true
            builder.disable(WRITE_DATES_AS_TIMESTAMPS);
            // if the client sends unknown props, just ignore them instead of failing
            builder.disable(FAIL_ON_UNKNOWN_PROPERTIES);
            // we still use joda DateTime...
            builder.addModule(new JodaModule());
        };
    }
}

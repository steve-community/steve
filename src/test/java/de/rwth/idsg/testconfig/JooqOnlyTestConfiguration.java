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
package de.rwth.idsg.testconfig;

import com.zaxxer.hikari.HikariDataSource;
import de.rwth.idsg.steve.config.BeanConfiguration;
import de.rwth.idsg.steve.config.SteveProperties;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Test configuration for tests that need the real Spring Boot property binding and
 * production jOOQ/DataSource bean wiring, but do not need to start the full SteVe
 * application context.
 *
 * This class intentionally lives outside {@code de.rwth.idsg.steve}. The
 * production {@link BeanConfiguration} component-scans that package, and full
 * {@code @SpringBootTest} tests would otherwise discover this helper as an
 * additional configuration class. That would register duplicate beans such as
 * {@code dataSource} when the full application context is started.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.05.2026
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({DataSourceProperties.class, SteveProperties.class})
public class JooqOnlyTestConfiguration {

    private final BeanConfiguration beanConfiguration = new BeanConfiguration();

    @Bean
    HikariDataSource dataSource(DataSourceProperties properties) {
        return beanConfiguration.dataSource(properties);
    }

    @Bean
    SQLDialect jooqSqlDialect(DataSource dataSource) {
        return beanConfiguration.jooqSqlDialect(dataSource);
    }

    @Bean
    DSLContext dslContext(DataSource dataSource,
                          SteveProperties steveProperties,
                          SQLDialect jooqSqlDialect) {
        return beanConfiguration.dslContext(dataSource, steveProperties, jooqSqlDialect);
    }
}

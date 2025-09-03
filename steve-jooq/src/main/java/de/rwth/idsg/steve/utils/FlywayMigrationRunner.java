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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.SteveConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.08.2021
 */
@Slf4j
@Component
public class FlywayMigrationRunner {

    private static final String INIT_SQL = "SET default_storage_engine=InnoDB;";
    private static final String BOOKKEEPING_TABLE = "schema_version";
    private static final String LOCATION_OF_MIGRATIONS = "classpath:db/migration";

    private final FluentConfiguration config;
    private final AtomicBoolean ran = new AtomicBoolean(false);

    public FlywayMigrationRunner(SteveConfiguration config) {
        this.config = createConfig(config);
    }

    @EventListener(ContextStartedEvent.class)
    public void onReady() {
        if (ran.compareAndSet(false, true)) {
            log.info("Starting Flyway migrations from {}", LOCATION_OF_MIGRATIONS);
            var result = config.load().migrate();
            log.info(
                    "Flyway migration complete: {} migrations executed, schema history table '{}'",
                    result.migrationsExecuted,
                    BOOKKEEPING_TABLE);
        } else {
            log.debug("Flyway migrations already executed; skipping.");
        }
    }

    /**
     * This configuration should be kept in-sync with the one of the flyway-maven-plugin in pom.xml
     */
    private static FluentConfiguration createConfig(SteveConfiguration config) {
        var dbConfig = config.getDb();
        var c = new FluentConfiguration();
        c.initSql(INIT_SQL);
        c.table(BOOKKEEPING_TABLE);
        c.cleanDisabled(true);
        c.schemas(dbConfig.getSchema());
        c.defaultSchema(dbConfig.getSchema());
        c.locations(LOCATION_OF_MIGRATIONS);
        c.dataSource(dbConfig.getJdbcUrl(), dbConfig.getUserName(), dbConfig.getPassword());
        return c;
    }
}

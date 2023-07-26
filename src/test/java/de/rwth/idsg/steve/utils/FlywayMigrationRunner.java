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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.SteveConfiguration;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.08.2021
 */
public class FlywayMigrationRunner {

    private static final String INIT_SQL = "SET default_storage_engine=InnoDB;";
    private static final String BOOKKEEPING_TABLE = "schema_version";
    private static final String LOCATION_OF_MIGRATIONS = "filesystem:src/main/resources/db/migration";

    public static void run(SteveConfiguration sc) {
        try {
            getConfig(sc.getDb()).load().migrate();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This configuration should be kept in-sync with the one of the flyway-maven-plugin in pom.xml
     */
    private static FluentConfiguration getConfig(SteveConfiguration.DB dbConfig) {
        return new FluentConfiguration()
                .initSql(INIT_SQL)
                .outOfOrder(true)
                .table(BOOKKEEPING_TABLE)
                .cleanDisabled(true)
                .schemas(dbConfig.getSchema())
                .defaultSchema(dbConfig.getSchema())
                .locations(LOCATION_OF_MIGRATIONS)
                .dataSource(dbConfig.getJdbcUrl(), dbConfig.getUserName(), dbConfig.getPassword());
    }
}

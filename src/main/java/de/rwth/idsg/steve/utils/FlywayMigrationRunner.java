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
    private static final String LOCATION_OF_MIGRATIONS = "db/migration";

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
        FluentConfiguration c = new FluentConfiguration();

        c.initSql(INIT_SQL);
        c.outOfOrder(true);
        c.table(BOOKKEEPING_TABLE);
        c.cleanDisabled(true);
        c.schemas(dbConfig.getSchema());
        c.defaultSchema(dbConfig.getSchema());
        c.locations(LOCATION_OF_MIGRATIONS);
        c.dataSource(dbConfig.getJdbcUrl(), dbConfig.getUserName(), dbConfig.getPassword());

        return c;
    }
}

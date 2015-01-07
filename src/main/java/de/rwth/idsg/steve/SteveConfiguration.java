package de.rwth.idsg.steve;

import org.jooq.SQLDialect;

import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public final class SteveConfiguration {
    private SteveConfiguration() {}

    // Current version of the application
    public static final String STEVE_VERSION = "1.0.2";
    // Mapping of Http requests that Spring Web MVC will handle
    public static final String SPRING_WEB_MAPPING = "/manager/*";
    // Mapping for CXF SOAP services
    public static final String CXF_MAPPING = "/services/*";

    /**
     * Jetty Configuration
     */
    public static final class Jetty {

        // Just to be backwards compatible with previous versions of Steve,
        // since there might be already configured chargepoints expecting the older path.
        // Otherwise, might as well be "/".
        public static final String CONTEXT_PATH = "/steve";

        public static final String SERVER_HOST = "localhost";
        public static final int SERVER_PORT = 8080;

        // SSL
        public static final boolean SSL_ENABLED = false;
        public static final int SSL_SERVER_PORT = 8443;
        public static final String KEY_STORE_PATH = "...";
        public static final String KEY_STORE_PASSWORD = "...";

        public static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
        public static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

        public static final int MIN_THREADS = 4;
        public static final int MAX_THREADS = 50;
    }

    /**
     * Database Configuration
     */
    public static final class DB {

        public static final String CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
        public static final String URL = "jdbc:mysql://localhost:3306/stevedb";
        public static final String USERNAME = "root";
        public static final String PASSWORD = "come47on";

        public static final SQLDialect JOOQ_DIALECT = SQLDialect.MYSQL;
        public static final boolean JOOQ_EXEC_LOGGING = false;
    }

}
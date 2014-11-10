package de.rwth.idsg.steve;

import org.jooq.SQLDialect;

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
     * Database Configuration
     */
    public static final class DB {
        public static final String DS_JNDI_NAME = "java:comp/env/jdbc/stevedb";
        public static final SQLDialect JOOQ_DIALECT = SQLDialect.MYSQL;
        public static final boolean JOOQ_EXEC_LOGGING = true;
    }

}
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public final class SteveConfiguration {
    private SteveConfiguration() { }

    // Root mapping for Spring
    public static final String SPRING_MAPPING = "/";
    // Web frontend
    public static final String SPRING_MANAGER_MAPPING = "/manager/*";
    // Mapping for CXF SOAP services
    public static final String CXF_MAPPING = "/services/*";
    // Just to be backwards compatible with previous versions of Steve,
    // since there might be already configured chargepoints expecting the older path.
    // Otherwise, might as well be "/".
    public static final String CONTEXT_PATH = "/steve";
    // Dummy service path
    public static final String ROUTER_ENDPOINT_PATH = "/CentralSystemService";

    // -------------------------------------------------------------------------
    // main.properties
    // -------------------------------------------------------------------------

    public static String STEVE_VERSION;
    public static ApplicationProfile PROFILE;

    /**
     * Jetty configuration
     */
    public static final class Jetty {
        public static String SERVER_HOST;
        public static boolean GZIP_ENABLED;

        // HTTP
        public static boolean HTTP_ENABLED;
        public static int HTTP_PORT;

        // HTTPS
        public static boolean HTTPS_ENABLED;
        public static int HTTPS_PORT;
        public static String KEY_STORE_PATH;
        public static String KEY_STORE_PASSWORD;
    }

    /**
     * Database configuration
     */
    public static final class DB {
        public static String IP;
        public static int PORT;
        public static String SCHEMA;
        public static String USERNAME;
        public static String PASSWORD;
        public static boolean SQL_LOGGING;
    }

    /**
     * Credentials for Web interface access
     */
    public static final class Auth {
        public static String USERNAME;
        public static String PASSWORD;
    }

    /**
     * OCPP-related configuration
     */
    public static final class Ocpp {
        public static WsSessionSelectStrategyEnum WS_SESSION_SELECT_STRATEGY;
    }

}

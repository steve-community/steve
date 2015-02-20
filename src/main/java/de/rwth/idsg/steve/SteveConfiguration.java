package de.rwth.idsg.steve;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public final class SteveConfiguration {
    private SteveConfiguration() {}

    // Mapping of Http requests that Spring Web MVC will handle
    public static final String SPRING_WEB_MAPPING = "/manager/*";
    // Mapping for CXF SOAP services
    public static final String CXF_MAPPING = "/services/*";
    // Just to be backwards compatible with previous versions of Steve,
    // since there might be already configured chargepoints expecting the older path.
    // Otherwise, might as well be "/".
    public static final String CONTEXT_PATH = "/steve";

    // -------------------------------------------------------------------------
    // main.properties
    // -------------------------------------------------------------------------

    public static String STEVE_VERSION;

    /**
     * Jetty configuration
     */
    public static final class Jetty {
        public static String SERVER_HOST;

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
        public static String URL;
        public static String USERNAME;
        public static String PASSWORD;
    }

    /**
     * Credentials for Web interface access
     */
    public static final class Auth {
        public static String USERNAME;
        public static String PASSWORD;
    }

}
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import de.rwth.idsg.steve.utils.PropertiesFileLoader;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
@Getter
public enum SteveConfiguration {
    CONFIG;

    // Root mapping for Spring
    private String springMapping = "/";
    // Web frontend
    private String springManagerMapping = "/manager/*";
    // Mapping for CXF SOAP services
    private String cxfMapping = "/services/*";
    // Dummy service path
    private String routerEndpointPath = "/CentralSystemService";
    // Time zone for the application and database connections
    private String timeZoneId = "UTC";  // or ZoneId.systemDefault().getId();

    // -------------------------------------------------------------------------
    // main.properties
    // -------------------------------------------------------------------------

    private String contextPath;
    private String steveVersion;
    private ApplicationProfile profile;
    private Ocpp ocpp;
    private Auth auth;
    private DB db;
    private Jetty jetty;

    SteveConfiguration() {
        PropertiesFileLoader p = new PropertiesFileLoader("main.properties");

        contextPath = sanitizeContextPath(p.getOptionalString("context.path"));
        steveVersion = p.getString("steve.version");
        profile = ApplicationProfile.fromName(p.getString("profile"));

        jetty = Jetty.builder()
                     .serverHost(p.getString("server.host"))
                     .gzipEnabled(p.getBoolean("server.gzip.enabled"))
                     .httpEnabled(p.getBoolean("http.enabled"))
                     .httpPort(p.getInt("http.port"))
                     .httpsEnabled(p.getBoolean("https.enabled"))
                     .httpsPort(p.getInt("https.port"))
                     .keyStorePath(p.getOptionalString("keystore.path"))
                     .keyStorePassword(p.getOptionalString("keystore.password"))
                     .build();

        db = DB.builder()
               .ip(p.getString("db.ip"))
               .port(p.getInt("db.port"))
               .schema(p.getString("db.schema"))
               .userName(p.getString("db.user"))
               .password(p.getString("db.password"))
               .sqlLogging(p.getBoolean("db.sql.logging"))
               .build();

        auth = Auth.builder()
                   .userName(p.getString("auth.user"))
                   .password(p.getString("auth.password"))
                   .build();

        ocpp = Ocpp.builder()
                   .autoRegisterUnknownStations(p.getOptionalBoolean("auto.register.unknown.stations"))
                   .wsSessionSelectStrategy(
                           WsSessionSelectStrategyEnum.fromName(p.getString("ws.session.select.strategy")))
                   .build();

        validate();
    }

    private String sanitizeContextPath(String s) {
        if (s == null || "/".equals(s)) {
            return "";

        } else if (s.startsWith("/")) {
            return s;

        } else {
            return "/" + s;
        }
    }

    private void validate() {
        if (!(jetty.httpEnabled || jetty.httpsEnabled)) {
            throw new IllegalArgumentException(
                    "HTTP and HTTPS are both disabled. Well, how do you want to access the server, then?");
        }
    }

    // -------------------------------------------------------------------------
    // Class declarations
    // -------------------------------------------------------------------------

    // Jetty configuration
    @Builder @Getter
    public static class Jetty {
        private String serverHost;
        private boolean gzipEnabled;

        // HTTP
        private boolean httpEnabled;
        private int httpPort;

        // HTTPS
        private boolean httpsEnabled;
        private int httpsPort;
        private String keyStorePath;
        private String keyStorePassword;
    }

    // Database configuration
    @Builder @Getter
    public static class DB {
        private String ip;
        private int port;
        private String schema;
        private String userName;
        private String password;
        private boolean sqlLogging;
    }

    // Credentials for Web interface access
    @Builder @Getter
    public static class Auth {
        private String userName;
        private String password;
    }

    // OCPP-related configuration
    @Builder @Getter
    public static class Ocpp {
        private boolean autoRegisterUnknownStations;
        private WsSessionSelectStrategyEnum wsSessionSelectStrategy;
    }

}

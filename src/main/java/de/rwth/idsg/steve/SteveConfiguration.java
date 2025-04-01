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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import de.rwth.idsg.steve.utils.PropertiesFileLoader;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
@Getter
public enum SteveConfiguration {
    CONFIG;

    // Root mapping for Spring
    private final String springMapping = "/";
    // Web frontend
    private final String springManagerMapping = "/manager";
    // Mapping for CXF SOAP services
    private final String cxfMapping = "/services";
    // Mapping for Web APIs
    private final String apiMapping = "/api";
    // Dummy service path
    private final String routerEndpointPath = "/CentralSystemService";
    // Time zone for the application and database connections
    private final String timeZoneId = "UTC";  // or ZoneId.systemDefault().getId();

    // -------------------------------------------------------------------------
    // application.properties
    // -------------------------------------------------------------------------

    private final String contextPath;
    private final String steveVersion;
    private final String gitDescribe;
    private final ApplicationProfile profile;
    private final Ocpp ocpp;
    private final Auth auth;
    private final WebApi webApi;
    private final DB db;
    private final Jetty jetty;

    SteveConfiguration() {
        PropertiesFileLoader p = new PropertiesFileLoader("application.properties");
        profile = ApplicationProfile.fromName(p.getString("profile"));
        PropertiesFileLoader overriden = new PropertiesFileLoader("application-" + profile.name().toLowerCase() + ".properties");

        contextPath = sanitizeContextPath(getOptionalString("context.path", p, overriden));
        steveVersion = getString("steve.version", p, overriden);
        gitDescribe = useFallbackIfNotSet(getOptionalString("git.describe", p, overriden), null);

        System.setProperty("spring.profiles.active", profile.name().toLowerCase());
        System.setProperty("logging.config", "classpath:logback-" + profile.name().toLowerCase() + ".xml");

        jetty = Jetty.builder()
                     .serverHost(getString("server.host", p, overriden))
                     .gzipEnabled(getBoolean("server.gzip.enabled", p, overriden))
                     .httpEnabled(getBoolean("http.enabled", p, overriden))
                     .httpPort(getInt("http.port", p, overriden))
                     .httpsEnabled(getBoolean("https.enabled", p, overriden))
                     .httpsPort(getInt("https.port", p, overriden))
                     .keyStorePath(getOptionalString("keystore.path", p, overriden))
                     .keyStorePassword(getOptionalString("keystore.password", p, overriden))
                     .build();

        db = DB.builder()
               .ip(getString("db.ip", p, overriden))
               .port(getInt("db.port", p, overriden))
               .schema(getString("db.schema", p, overriden))
               .userName(getString("db.user", p, overriden))
               .password(getString("db.password", p, overriden))
               .sqlLogging(getBoolean("db.sql.logging", p, overriden))
               .build();

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        auth = Auth.builder()
                   .passwordEncoder(encoder)
                   .userName(getString("auth.user", p, overriden))
                   .encodedPassword(encoder.encode(getString("auth.password", p, overriden)))
                   .build();

        webApi = WebApi.builder()
                       .headerKey(getOptionalString("webapi.key", p, overriden))
                       .headerValue(getOptionalString("webapi.value", p, overriden))
                       .build();

        ocpp = Ocpp.builder()
                   .autoRegisterUnknownStations(getOptionalBoolean("auto.register.unknown.stations", p, overriden))
                   .chargeBoxIdValidationRegex(getOptionalString("charge-box-id.validation.regex", p, overriden))
                   .wsSessionSelectStrategy(
                           WsSessionSelectStrategyEnum.fromName(getString("ws.session.select.strategy", p, overriden)))
                   .build();

        validate();
    }

    private static String getOptionalString(String key, PropertiesFileLoader p, PropertiesFileLoader overriden) {
      String value = overriden.getOptionalString(key);
      if (value == null) {
          return p.getOptionalString(key);
      }
      return value;
    }

    private static String getString(String key, PropertiesFileLoader p, PropertiesFileLoader overriden) {
      String value = overriden.getOptionalString(key);
      if (value == null) {
          return p.getString(key);
      }
      return value;
    }

    private static boolean getOptionalBoolean(String key, PropertiesFileLoader p, PropertiesFileLoader overriden) {
        if (overriden.getOptionalString(key) == null) {
            return p.getOptionalBoolean(key);
        } else {
            return overriden.getOptionalBoolean(key);
        }
    }

    private static boolean getBoolean(String key, PropertiesFileLoader p, PropertiesFileLoader overriden) {
        if (overriden.getOptionalString(key) == null) {
            return p.getBoolean(key);
        } else {
            return overriden.getOptionalBoolean(key);
        }
    }

    private static int getInt(String key, PropertiesFileLoader p, PropertiesFileLoader overriden) {
        if (overriden.getOptionalString(key) == null) {
            return p.getInt(key);
        } else {
            return overriden.getInt(key);
        }
    }

    public String getSteveCompositeVersion() {
        if (gitDescribe == null) {
            return steveVersion;
        } else {
            return steveVersion + "-g" + gitDescribe;
        }
    }

    private static String useFallbackIfNotSet(String value, String fallback) {
        if (value == null) {
            // if the property is optional, value will be null
            return fallback;
        } else if (value.startsWith("${")) {
            // property value variables start with "${" (if maven is not used, the value will not be set)
            return fallback;
        } else {
            return value;
        }
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
        private final String serverHost;
        private final boolean gzipEnabled;

        // HTTP
        private final boolean httpEnabled;
        private final int httpPort;

        // HTTPS
        private final boolean httpsEnabled;
        private final int httpsPort;
        private final String keyStorePath;
        private final String keyStorePassword;
    }

    // Database configuration
    @Builder @Getter
    public static class DB {
        private final String ip;
        private final int port;
        private final String schema;
        private final String userName;
        private final String password;
        private final boolean sqlLogging;
    }

    // Credentials for Web interface access
    @Builder @Getter
    public static class Auth {
        private final PasswordEncoder passwordEncoder;
        private final String userName;
        private final String encodedPassword;
    }

    @Builder @Getter
    public static class WebApi {
        private final String headerKey;
        private final String headerValue;
    }

    // OCPP-related configuration
    @Builder @Getter
    public static class Ocpp {
        private final boolean autoRegisterUnknownStations;
        private final String chargeBoxIdValidationRegex;
        private final WsSessionSelectStrategy wsSessionSelectStrategy;
    }

}

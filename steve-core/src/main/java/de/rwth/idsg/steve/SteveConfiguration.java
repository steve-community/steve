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

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
@Builder @Getter
public class SteveConfiguration {

    @Builder @Getter
    public static class Paths {
        // Root mapping for Spring
        private final String rootMapping;
        // Web frontend
        private final String managerMapping;
        // Mapping for SOAP services
        private final String soapMapping;
        // Mapping for Websocket services
        private final String websocketMapping;
        // Mapping for Web APIs
        private final String apiMapping;
        // Dummy service path
        private final String routerEndpointPath;

        private final String contextPath;
    }

    private final Paths paths;
    // Time zone for the application and database connections
    private final String timeZoneId;
    private final String steveVersion;
    private final String gitDescribe;
    private final ApplicationProfile profile;
    private final Ocpp ocpp;
    private final Auth auth;
    private final WebApi webApi;
    private final DB db;
    private final Jetty jetty;

    public String getSteveCompositeVersion() {
        if (gitDescribe == null) {
            return steveVersion;
        } else {
            return steveVersion + "-g" + gitDescribe;
        }
    }

    public void postConstruct() {
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
        private final String jdbcUrl;
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
        private final String wsSessionSelectStrategy;
    }
}

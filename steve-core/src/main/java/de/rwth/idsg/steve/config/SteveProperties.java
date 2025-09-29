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
package de.rwth.idsg.steve.config;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "steve")
public class SteveProperties {

    @Data
    public static class Paths {
        // Root mapping for Spring
        private String rootMapping = "/";
        // Web frontend
        private String managerMapping = "/manager";
        // Mapping for SOAP services
        private String soapMapping = "/services";
        // Mapping for Websocket services
        private String websocketMapping = "/websocket";
        // Mapping for Web APIs
        private String apiMapping = "/api";
        // Dummy service path
        private String routerEndpointPath = "/CentralSystemService";

        private @Nullable String contextPath;
    }

    private Paths paths = new Paths();
    // Time zone for the application and database connections
    private String timeZoneId;
    private String steveVersion;
    private String profile;
    private Ocpp ocpp = new Ocpp();
    private Auth auth = new Auth();
    private WebApi webApi = new WebApi();
    private Jooq jooq = new Jooq();

    @Data
    public static class Jooq {
        private boolean executiveLogging;
        private String schemaSource;
        private @Nullable String schema;
    }

    @Data
    public static class Auth {
        private String username;
        private String password;
    }

    @Data
    public static class WebApi {
        private @Nullable String headerKey;
        private @Nullable String headerValue;
    }

    @Data
    public static class Ocpp {
        private boolean autoRegisterUnknownStations;
        private @Nullable String chargeBoxIdValidationRegex;
        private String wsSessionSelectStrategy;
    }
}

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

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import lombok.Data;
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

    // Web frontend
    public static final String SPRING_MANAGER_MAPPING = "/manager";
    // Mapping for CXF SOAP services
    public static final String CXF_MAPPING = "/services";
    // Mapping for Web APIs
    public static final String API_MAPPING = "/api";
    // Dummy service path
    public static final String ROUTER_ENDPOINT_PATH = "/CentralSystemService";
    // Time zone for the application and database connections
    public static final String TIME_ZONE_ID = "UTC";  // or ZoneId.systemDefault().getId();

    String version;
    Auth auth = new Auth();
    Jooq jooq = new Jooq();
    Ocpp ocpp = new Ocpp();

    @Data
    public static class Jooq {
        boolean executiveLogging;
    }

    @Data
    public static class Auth {
        String username;
        String password;
        String webApiKey;
        String webApiSecret;
    }

    @Data
    public static class Ocpp {
        WsSessionSelectStrategyEnum wsSessionSelectStrategy;
        boolean autoRegisterUnknownStations;
        String chargeBoxIdValidationRegex;
        Security security = new Security();

        @Data
        public static class Security {
            private int profile;
            private int certificateValidityYears;
            private String clientCertHeaderFromProxy;

            public boolean requiresTls() {
                return profile >= 2;
            }
        }
    }
}

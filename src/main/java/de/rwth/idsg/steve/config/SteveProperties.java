/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import org.apache.commons.lang3.StringUtils;
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
        Protocols enabledProtocols;
        Security security = new Security();

        @Data
        public static class Protocols {
            Transports v12;
            Transports v15;
            Transports v16;

            @Data
            public static class Transports {
                boolean soap;
                boolean json;

                public boolean isEnabled() {
                    return soap || json;
                }
            }
        }

        @Data
        public static class Security {
            private String clientCertHeaderFromProxy;
            private CsrSigning csrSigning = new CsrSigning();

            @Data
            public static class CsrSigning {
                private String provider;
                private LocalCsrSigning providerLocal = new LocalCsrSigning();

                @Data
                public static class LocalCsrSigning {
                    private Integer certificateValidityYears;
                    private String caCertificatePem;
                    private String caKeyPem;
                    private String caChainPem;
                    private SignatureAlgorithmPolicy signatureAlgorithmPolicy = SignatureAlgorithmPolicy.AUTO;

                    public enum SignatureAlgorithmPolicy {
                        AUTO,
                        RSA_PSS,
                        RSA_PKCS1
                    }

                    public boolean isValid() {
                        return certificateValidityYears != null
                            && !StringUtils.isBlank(caCertificatePem)
                            && !StringUtils.isBlank(caKeyPem);
                    }
                }
            }
        }
    }
}

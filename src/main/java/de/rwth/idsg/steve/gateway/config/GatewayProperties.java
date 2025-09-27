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
package de.rwth.idsg.steve.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "steve.gateway")
public class GatewayProperties {

    private boolean enabled = false;

    private Ocpi ocpi = new Ocpi();
    private Oicp oicp = new Oicp();

    @Data
    public static class Ocpi {
        private boolean enabled = false;
        private String version = "2.2";
        private String countryCode;
        private String partyId;
        private String baseUrl;
        private String currency = "EUR";
        private Authentication authentication = new Authentication();
        private CurrencyConversion currencyConversion = new CurrencyConversion();
    }

    @Data
    public static class Oicp {
        private boolean enabled = false;
        private String version = "2.3";
        private String providerId;
        private String baseUrl;
        private Authentication authentication = new Authentication();
    }

    @Data
    public static class Authentication {
        private String token;
        private String apiKey;
    }

    @Data
    public static class CurrencyConversion {
        private boolean enabled = false;
        private String apiKey;
        private String apiUrl = "https://api.exchangerate-api.com/v4/latest/";
    }
}
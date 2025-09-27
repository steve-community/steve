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
package de.rwth.idsg.steve.gateway.ocpi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Credentials {

    private String token;

    private String url;

    private List<CredentialsRole> roles;

    @Data
    @Builder
    public static class CredentialsRole {

        private String role;

        @JsonProperty("business_details")
        private BusinessDetails businessDetails;

        @JsonProperty("party_id")
        private String partyId;

        @JsonProperty("country_code")
        private String countryCode;
    }

    @Data
    @Builder
    public static class BusinessDetails {

        private String name;

        private String website;

        private String logo;
    }
}
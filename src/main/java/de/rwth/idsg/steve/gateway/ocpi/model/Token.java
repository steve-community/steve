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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * OCPI v2.2 Token model
 *
 * @author Steve Community
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("party_id")
    private String partyId;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("type")
    private TokenType type;

    @JsonProperty("contract_id")
    private String contractId;

    @JsonProperty("visual_number")
    private String visualNumber;

    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("valid")
    private Boolean valid;

    @JsonProperty("whitelist")
    private WhitelistType whitelist;

    @JsonProperty("language")
    private String language;

    @JsonProperty("default_profile_type")
    private ProfileType defaultProfileType;

    @JsonProperty("energy_contract")
    private EnergyContract energyContract;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime lastUpdated;
}
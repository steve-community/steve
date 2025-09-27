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
package de.rwth.idsg.steve.gateway.oicp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

/**
 * OICP v2.3 AuthorizationStart model
 *
 * @author Steve Community
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationStart {

    @JsonProperty("OperatorID")
    private String operatorId;

    @JsonProperty("Identification")
    private Identification identification;

    @JsonProperty("EvseID")
    private String evseId;

    @JsonProperty("PartnerProductID")
    private String partnerProductId;

    @JsonProperty("SessionID")
    private String sessionId;

    @JsonProperty("CPOPartnerSessionID")
    private String cpoPartnerSessionId;

    @JsonProperty("EMPPartnerSessionID")
    private String empPartnerSessionId;

    @JsonProperty("RequestTimeStamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private DateTime requestTimeStamp;
}
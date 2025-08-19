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
package de.rwth.idsg.steve.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.03.2015
 */
@Getter
@Builder
@ToString
public final class OcppJsonStatus {
    @JsonIgnore
    @Schema(description = "Charge Box Pk", hidden = true)
    private final int chargeBoxPk;
    @Schema(description = "Charge Box Id")
    private final String chargeBoxId;
    @JsonIgnore
    @Schema(description = "Connected since", hidden = true)
    private final String connectedSince;
    @Schema(description = "Duration of the Connection")
    private final String connectionDuration;
    @Schema(description = "Ocpp version")
    private final OcppVersion version;
    @Schema(description = "Connected since (ISO-8601 timestamp)")
    private final DateTime connectedSinceDT;
}

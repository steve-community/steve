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
package de.rwth.idsg.steve.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
@Getter
@Builder
public final class ConnectorStatus {
    @JsonIgnore
    @Schema(description = "Charge Box DB key", hidden = true)
    private final int chargeBoxPk;
    @Schema(description = "Charge Box ID")
    private final String chargeBoxId;
    @Schema(description = "Connector ID")
    private final int connectorId;

    @Schema(description = "Status")
    private final String status;
    @Schema(description = "Error code")
    private final String errorCode;

    @Schema(description = "Timestamp")
    private final String timeStamp;
    // For additional internal processing. Not related to the humanized
    // String version above, which is for representation on frontend
    @Schema(description = "Timestamp of the status")
    private final DateTime statusTimestamp;

    @Schema(description = "OCPP version")
    private final OcppProtocol ocppProtocol;

    // This is true, if the chargeBox this connector belongs to is a WS/JSON station
    // and it is disconnected at the moment of building this DTO.
    @Schema(description = "Json and Disconnected")
    @Setter
    @Builder.Default
    private boolean jsonAndDisconnected = false;
}

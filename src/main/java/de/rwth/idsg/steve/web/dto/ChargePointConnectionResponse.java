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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Current transport connection state of a charge point, aggregated over its
 * connectors.
 *
 * @author huangkongsheng
 * @since 19.10.2026
 */
@Schema(description = "Transport connection state of a charge point")
public record ChargePointConnectionResponse(
    String chargeBoxId,
    @Schema(description = "True when the station is currently connected on the WebSocket transport. "
        + "For SOAP stations no transport-level state is tracked and this is always true.")
    boolean connected,
    String ocppProtocol,
    String lastHeartbeatTimestamp,
    List<ConnectorStatusResponse> connectors
) {

    public static ChargePointConnectionResponse of(ConnectorStatus first, List<ConnectorStatus> connectors) {
        return new ChargePointConnectionResponse(
            first.getChargeBoxId(),
            connectors.stream().anyMatch(s -> !s.isJsonAndDisconnected()),
            first.getOcppProtocol().getVersion().getValue(),
            first.getTimeStamp(),
            connectors.stream().map(ConnectorStatusResponse::of).toList()
        );
    }
}

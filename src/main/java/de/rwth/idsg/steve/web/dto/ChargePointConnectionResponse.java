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

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Current transport connection state of a charge point, aggregated over its
 * connectors. The heartbeat and protocol are taken from the charge point
 * itself, not from connector status rows, since a connector status update is
 * neither a heartbeat nor does every station report connector statuses.
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
    @Schema(description = "Composite protocol identifier of the station, e.g. OCPP16J (JSON) or "
        + "OCPP16S (SOAP). Null if the station has no protocol assigned yet.")
    String ocppProtocol,
    DateTime lastHeartbeatTimestamp,
    List<ChargePointConnectorResponse> connectors
) {

    public static ChargePointConnectionResponse of(ChargePoint.Overview overview, List<ConnectorStatus> connectors) {
        return new ChargePointConnectionResponse(
            overview.getChargeBoxId(),
            connectors.stream().anyMatch(s -> !s.isJsonAndDisconnected()),
            overview.getOcppProtocol(),
            overview.getLastHeartbeatTimestampDT(),
            connectors.stream()
                      .filter(s -> s.getConnectorId() > 0)
                      .map(ChargePointConnectorResponse::of)
                      .toList()
        );
    }
}

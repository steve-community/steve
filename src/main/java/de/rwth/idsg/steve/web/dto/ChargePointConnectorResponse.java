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

/**
 * The last known OCPP status of a single connector, together with the current
 * transport-layer connection state. The two are distinct: a station that is
 * currently disconnected from the WebSocket transport still reports its last
 * known OCPP status.
 *
 * @author huangkongsheng
 * @since 19.10.2026
 */
@Schema(description = "Last known status of a charge point connector")
public record ChargePointConnectorResponse(
    int connectorId,
    @Schema(description = "Last known OCPP status reported by the station (e.g. Available, Charging, Faulted)")
    String status,
    String errorCode,
    @Schema(description = "True when the station is currently connected on the WebSocket transport. "
        + "For SOAP stations no transport-level state is tracked and this is always true.")
    boolean transportConnected,
    String statusTimestamp
) {

    public static ChargePointConnectorResponse of(ConnectorStatus s) {
        return new ChargePointConnectorResponse(
            s.getConnectorId(),
            s.getStatus(),
            s.getErrorCode(),
            !s.isJsonAndDisconnected(),
            s.getStatusTimestamp()
        );
    }
}

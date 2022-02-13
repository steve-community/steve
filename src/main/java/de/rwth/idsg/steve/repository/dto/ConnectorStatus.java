/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
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
    private final String chargeBoxId, timeStamp, status, errorCode;
    private final int chargeBoxPk, connectorId;

    // For additional internal processing. Not related to the humanized
    // String version above, which is for representation on frontend
    private final DateTime statusTimestamp;

    private final OcppProtocol ocppProtocol;

    // This is true, if the chargeBox this connector belongs to is a WS/JSON station
    // and it is disconnected at the moment of building this DTO.
    @Setter
    @Builder.Default
    private boolean jsonAndDisconnected = false;
}

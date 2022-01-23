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
package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.01.2015
 */
@Getter
public class RemoteStartTransactionParams extends SingleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotBlank(message = "User ID Tag is required")
    @IdTag
    @Setter private String idTag;

    /**
     * Not for a specific connector, when frontend sends the value 0.
     * This corresponds to not to include the connector id parameter in OCPP request.
     */
    public void setConnectorId(Integer connectorId) {
        if (connectorId == 0) {
            this.connectorId = null;
        } else {
            this.connectorId = connectorId;
        }
    }
}

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

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.03.2018
 */
@Getter
public class ChangeAvailabilityParams extends MultipleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotNull(message = "Availability Type is required")
    @Setter private AvailabilityType availType;

    /**
     * if empty, 0 = charge point as a whole
     */
    public void setConnectorId(Integer connectorId) {
        this.connectorId = Objects.requireNonNullElse(connectorId, 0);
    }
}

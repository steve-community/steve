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

import de.rwth.idsg.steve.utils.ConnectorStatusFilter;
import de.rwth.idsg.steve.web.validation.ChargeBoxId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.09.2018
 */
@Getter
@Setter
@ToString
public class ConnectorStatusForm {
    @ChargeBoxId
    private String chargeBoxId;

    @Schema(allowableValues = {"Available", "Charging", "Faulted", "Finishing", "Occupied", "Preparing", "Reserved", "SuspendedEV", "SuspendedEVSE", "Unavailable"})
    private String status;

    @Schema(defaultValue = "ReplaceWithStation",
        description = """
        How to handle the status of connector 0 (the whole station) if it is more recent than the status of
        the other connectors of the station.
        - ReplaceWithStation: return only connector 0 (default).
        - InheritFromStation: return the other connectors with the status of connector 0.
        - AsReported: no interpretation, return the latest status of every connector including 0.
        
        In all cases, only the latest status per connector is returned (no history).
        The status filter is applied to the result of this strategy.
        """)
    private ConnectorStatusFilter.Strategy connectorZeroStrategy = ConnectorStatusFilter.DEFAULT_STRATEGY;
}

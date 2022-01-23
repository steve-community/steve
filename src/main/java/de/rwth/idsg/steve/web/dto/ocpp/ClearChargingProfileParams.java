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
import ocpp.cp._2015._10.ChargingProfilePurposeType;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.11.2018
 */
@Getter
@Setter
public class ClearChargingProfileParams extends MultipleChargePointSelect {

    @NotNull(message = "Filter Type is required")
    private ClearChargingProfileFilterType filterType = ClearChargingProfileFilterType.ChargingProfileId;

    @Positive
    private Integer chargingProfilePk;

    // A connectorId of zero (0) specifies the charging profile for the overall Charge Point.
    // Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    private ChargingProfilePurposeType chargingProfilePurpose;

    private Integer stackLevel;

    @AssertTrue(message = "When filtering by id, charging profile id must be set")
    public boolean isValidWhenFilterById() {
        return filterType != ClearChargingProfileFilterType.ChargingProfileId || chargingProfilePk != null;
    }

}

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
package de.rwth.idsg.steve.service.dto;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
@Getter
@RequiredArgsConstructor
public class EnhancedSetChargingProfileParams implements ChargePointSelection {

    private final SetChargingProfileParams delegate;
    private final ChargingProfile.Details details;

    @Override
    public List<ChargePointSelect> getChargePointSelectList() {
        return delegate.getChargePointSelectList();
    }
}

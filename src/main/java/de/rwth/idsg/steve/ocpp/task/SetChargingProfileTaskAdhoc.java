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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import ocpp.cp._2015._10.SetChargingProfileRequest;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 16.02.2025
 */
public class SetChargingProfileTaskAdhoc extends SetChargingProfileTask {

    private final SetChargingProfileRequest request;

    public SetChargingProfileTaskAdhoc(MultipleChargePointSelect params,
                                       SetChargingProfileRequest request) {
        super(params);
        this.request = request;
        checkAdditionalConstraints(request);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public SetChargingProfileRequest getOcpp16Request() {
        return request;
    }
}

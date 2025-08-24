/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.SetChargingProfileRequest;
import ocpp.cp._2015._10.SetChargingProfileResponse;

import jakarta.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 06.02.2025
 */
public abstract class SetChargingProfileTask extends Ocpp16AndAboveTask<MultipleChargePointSelect, String> {

    public SetChargingProfileTask(MultipleChargePointSelect params) {
        super(params);
    }

    public abstract SetChargingProfileRequest getOcpp16Request();

    @Override
    public AsyncHandler<SetChargingProfileResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    /**
     * Do some additional checks defined by OCPP spec, which cannot be captured with javax.validation
     */
    protected static void checkAdditionalConstraints(SetChargingProfileRequest request) {
        ChargingProfilePurposeType purpose = request.getCsChargingProfiles().getChargingProfilePurpose();

        if (ChargingProfilePurposeType.CHARGE_POINT_MAX_PROFILE == purpose
            && request.getConnectorId() != 0) {
            throw new SteveException("ChargePointMaxProfile can only be set at Charge Point ConnectorId 0");
        }

        if (ChargingProfilePurposeType.TX_PROFILE == purpose
            && request.getConnectorId() < 1) {
            throw new SteveException("TxProfile should only be set at Charge Point ConnectorId > 0");
        }
    }
}

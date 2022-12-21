/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileFilterType;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.service.cs.ChargingProfileService;
import ocpp.cp._2015._10.ClearChargingProfileRequest;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
public class ClearChargingProfileTask extends Ocpp16AndAboveTask<ClearChargingProfileParams, String> {

    private final ChargingProfileService chargingProfileRepository;

    public ClearChargingProfileTask(OcppVersion ocppVersion,
                                    ClearChargingProfileParams params,
                                    ChargingProfileService chargingProfileRepository) {
        super(ocppVersion, params);
        this.chargingProfileRepository = chargingProfileRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String statusValue, boolean remote) {
                addNewResponse(chargeBoxId, statusValue);

                if (!remote && "Accepted".equalsIgnoreCase(statusValue)) {
                    switch (params.getFilterType()) {
                        case ChargingProfileId:
                            chargingProfileRepository.clearProfile(params.getChargingProfilePk(), chargeBoxId);
                            break;
                        case OtherParameters:
                            chargingProfileRepository.clearProfile(chargeBoxId,
                                    params.getConnectorId(), params.getChargingProfilePurpose(), params.getStackLevel());
                            break;
                        default:
                            log.warn("Unexpected {} enum value", ClearChargingProfileFilterType.class.getSimpleName());
                    }
                }
            }
        };
    }

    @Override
    public ocpp.cp._2015._10.ClearChargingProfileRequest getOcpp16Request() {
        return new ClearChargingProfileRequest()
                .withId(params.getChargingProfilePk())
                .withConnectorId(params.getConnectorId())
                .withChargingProfilePurpose(params.getChargingProfilePurpose())
                .withStackLevel(params.getStackLevel());
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ClearChargingProfileResponse> getOcpp16Handler(String chargeBoxId, boolean remote) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value(),remote);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

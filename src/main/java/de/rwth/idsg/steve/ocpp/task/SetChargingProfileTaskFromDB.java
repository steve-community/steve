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
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.utils.mapper.ChargingProfileDetailsMapper;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import jooq.steve.db.tables.records.ChargingProfileRecord;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import ocpp.cp._2015._10.RecurrencyKindType;
import ocpp.cp._2015._10.SetChargingProfileRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
public class SetChargingProfileTaskFromDB extends SetChargingProfileTask {

    private final int connectorId;
    private final Integer transactionId;
    private final ChargingProfile.Details details;
    private final ChargingProfileRepository chargingProfileRepository;

    public SetChargingProfileTaskFromDB(SetChargingProfileParams params,
                                        ChargingProfile.Details details,
                                        ChargingProfileRepository chargingProfileRepository) {
        super(params);
        this.connectorId = params.getConnectorId();
        this.transactionId = params.getTransactionId();
        this.details = details;
        this.chargingProfileRepository = chargingProfileRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                ChargingProfilePurposeType purpose = ChargingProfilePurposeType.fromValue(details.getProfile().getChargingProfilePurpose());
                addNewResponse(chargeBoxId, statusValue);

                if ("Accepted".equalsIgnoreCase(statusValue) && ChargingProfilePurposeType.TX_PROFILE != purpose) {
                    int chargingProfilePk = details.getProfile().getChargingProfilePk();
                    chargingProfileRepository.setProfile(chargingProfilePk, chargeBoxId, connectorId);
                }
            }
        };
    }

    @Override
    public SetChargingProfileRequest getOcpp16Request() {
        ocpp.cp._2015._10.ChargingProfile ocppProfile = ChargingProfileDetailsMapper.mapToOcpp(details, transactionId);

        var request = new SetChargingProfileRequest()
                .withConnectorId(connectorId)
                .withCsChargingProfiles(ocppProfile);

        checkAdditionalConstraints(request);

        return request;
    }
}

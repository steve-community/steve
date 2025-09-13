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

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileFilterType;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ClearChargingProfileRequest;
import ocpp.cp._2015._10.ClearChargingProfileResponse;

import java.util.Map;

@Slf4j
public class ClearChargingProfileTask extends CommunicationTask<ClearChargingProfileParams, String> {

    private static final TaskDefinition<ClearChargingProfileParams, String> TASK_DEFINITION =
            TaskDefinition.<ClearChargingProfileParams, String>builder()
                    .versionHandlers(Map.of(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ClearChargingProfileRequest()
                                            .withId(task.getParams().getChargingProfilePk())
                                            .withConnectorId(task.getParams().getConnectorId())
                                            .withChargingProfilePurpose(
                                                    task.getParams().getChargingProfilePurpose())
                                            .withStackLevel(task.getParams().getStackLevel()),
                                    (ClearChargingProfileResponse r) ->
                                            r.getStatus().value())))
                    .build();

    public ClearChargingProfileTask(
            ClearChargingProfileParams params, ChargingProfileRepository chargingProfileRepository, String caller) {
        super(params, caller, TASK_DEFINITION);

        addCallback(new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);

                switch (getParams().getFilterType()) {
                    case ChargingProfileId ->
                        chargingProfileRepository.clearProfile(getParams().getChargingProfilePk(), chargeBoxId);
                    case OtherParameters ->
                        chargingProfileRepository.clearProfile(
                                chargeBoxId,
                                getParams().getConnectorId(),
                                getParams().getChargingProfilePurpose(),
                                getParams().getStackLevel());
                    default -> {
                        log.warn("Unexpected {} enum value", ClearChargingProfileFilterType.class.getSimpleName());
                        return;
                    }
                }

                if (!"Accepted".equalsIgnoreCase(statusValue)) {
                    log.info(
                            "Deleted charging profile(s) for chargebox '{}' from DB even though the response was {}",
                            chargeBoxId,
                            statusValue);
                }
            }

            @Override
            public void successError(String chargeBoxId, Object error) {
                addNewError(chargeBoxId, error.toString());
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
            }
        });
    }

    public ClearChargingProfileTask(
            ClearChargingProfileParams params, ChargingProfileRepository chargingProfileRepository) {
        this(params, chargingProfileRepository, "SteVe");
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, String response) {
                addNewResponse(chargeBoxId, response);
            }

            @Override
            public void successError(String chargeBoxId, Object error) {
                addNewError(chargeBoxId, error.toString());
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
            }
        };
    }
}

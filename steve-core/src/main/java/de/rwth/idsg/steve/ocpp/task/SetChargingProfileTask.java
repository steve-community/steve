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
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import lombok.Getter;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import ocpp.cp._2015._10.RecurrencyKindType;
import ocpp.cp._2015._10.SetChargingProfileRequest;
import ocpp.cp._2015._10.SetChargingProfileResponse;

import java.util.Map;
import java.util.Optional;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toOffsetDateTime;

@Getter
public class SetChargingProfileTask extends CommunicationTask<SetChargingProfileParams, String> {

    private static final TaskDefinition<SetChargingProfileParams, String> TASK_DEFINITION =
            TaskDefinition.<SetChargingProfileParams, String>builder()
                    .versionHandlers(Map.of(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> ((SetChargingProfileTask) task).getRequest(),
                                    (SetChargingProfileResponse response) ->
                                            response.getStatus().value())))
                    .build();

    private final SetChargingProfileRequest request;

    /**
     * Constructor for ad-hoc SetChargingProfile task
     */
    public SetChargingProfileTask(SetChargingProfileParams params, SetChargingProfileRequest request, String caller) {
        super(params, caller, TASK_DEFINITION);
        this.request = request;
        checkAdditionalConstraints(this.request);
    }

    /**
     * Constructor for SetChargingProfile task from database
     */
    public SetChargingProfileTask(
            SetChargingProfileParams params,
            ChargingProfile.Details details,
            ChargingProfileRepository repo,
            String caller) {
        super(params, caller, TASK_DEFINITION);
        this.request = buildRequestFromDb(params, details);
        checkAdditionalConstraints(this.request);

        // Add custom callback to update DB
        addCallback(new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);
                if ("Accepted".equalsIgnoreCase(statusValue)) {
                    repo.setProfile(details.getChargingProfilePk(), chargeBoxId, params.getConnectorId());
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

    private SetChargingProfileRequest buildRequestFromDb(
            SetChargingProfileParams params, ChargingProfile.Details details) {
        var schedulePeriods = details.getPeriods().stream()
                .map(k -> {
                    ChargingSchedulePeriod p = new ChargingSchedulePeriod();
                    p.setStartPeriod(k.getStartPeriodInSeconds());
                    p.setLimit(k.getPowerLimit());
                    p.setNumberPhases(k.getNumberPhases());
                    return p;
                })
                .toList();

        var schedule = new ChargingSchedule()
                .withDuration(details.getDurationInSeconds())
                .withStartSchedule(toOffsetDateTime(details.getStartSchedule()))
                .withChargingRateUnit(ChargingRateUnitType.fromValue(details.getChargingRateUnit()))
                .withMinChargingRate(details.getMinChargingRate())
                .withChargingSchedulePeriod(schedulePeriods);

        var ocppProfile = new ocpp.cp._2015._10.ChargingProfile()
                .withChargingProfileId(details.getChargingProfilePk())
                .withStackLevel(details.getStackLevel())
                .withChargingProfilePurpose(ChargingProfilePurposeType.fromValue(details.getChargingProfilePurpose()))
                .withChargingProfileKind(ChargingProfileKindType.fromValue(details.getChargingProfileKind()))
                .withRecurrencyKind(Optional.ofNullable(details.getRecurrencyKind())
                        .map(RecurrencyKindType::fromValue)
                        .orElse(null))
                .withValidFrom(toOffsetDateTime(details.getValidFrom()))
                .withValidTo(toOffsetDateTime(details.getValidTo()))
                .withChargingSchedule(schedule);

        return new SetChargingProfileRequest()
                .withConnectorId(params.getConnectorId())
                .withCsChargingProfiles(ocppProfile);
    }

    private static void checkAdditionalConstraints(SetChargingProfileRequest request) {
        Optional.ofNullable(request.getCsChargingProfiles())
                .map(ocpp.cp._2015._10.ChargingProfile::getChargingProfilePurpose)
                .ifPresent(purpose -> {
                    if (purpose == ChargingProfilePurposeType.CHARGE_POINT_MAX_PROFILE
                            && request.getConnectorId() != 0) {
                        throw new SteveException.InternalError(
                                "ChargePointMaxProfile can only be set at Charge Point ConnectorId 0");
                    }

                    if (purpose == ChargingProfilePurposeType.TX_PROFILE && request.getConnectorId() < 1) {
                        throw new SteveException.InternalError(
                                "TxProfile should only be set at Charge Point ConnectorId > 0");
                    }
                });
    }
}

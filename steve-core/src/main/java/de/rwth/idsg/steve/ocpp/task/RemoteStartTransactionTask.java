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
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import ocpp.cp._2015._10.ChargingProfile;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.jspecify.annotations.Nullable;

public class RemoteStartTransactionTask
        extends CommunicationTask<RemoteStartTransactionTask.RemoteStartTransactionWithProfileParams, String> {

    private static final TaskDefinition<RemoteStartTransactionWithProfileParams, String> TASK_DEFINITION =
            TaskDefinition.<RemoteStartTransactionWithProfileParams, String>builder()
                    .versionHandler(
                            OcppVersion.V_12,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2010._08.RemoteStartTransactionRequest()
                                            .withIdTag(task.getParams().getIdTag())
                                            .withConnectorId(task.getParams().getConnectorId()),
                                    (ocpp.cp._2010._08.RemoteStartTransactionResponse r) ->
                                            r.getStatus().value()))
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2012._06.RemoteStartTransactionRequest()
                                            .withIdTag(task.getParams().getIdTag())
                                            .withConnectorId(task.getParams().getConnectorId()),
                                    (ocpp.cp._2012._06.RemoteStartTransactionResponse r) ->
                                            r.getStatus().value()))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.RemoteStartTransactionRequest()
                                            .withIdTag(task.getParams().getIdTag())
                                            .withConnectorId(task.getParams().getConnectorId())
                                            .withChargingProfile(task.getParams().chargingProfile),
                                    (ocpp.cp._2015._10.RemoteStartTransactionResponse r) ->
                                            r.getStatus().value()))
                    .build();

    public RemoteStartTransactionTask(
            RemoteStartTransactionParams params, ChargingProfileRepository chargingProfileRepository) {
        super(
                TASK_DEFINITION,
                new RemoteStartTransactionWithProfileParams(
                        params, createChargingProfile(params.getChargingProfilePk(), chargingProfileRepository)));
    }

    public RemoteStartTransactionTask(
            RemoteStartTransactionParams params, String caller, ChargingProfileRepository chargingProfileRepository) {
        super(
                TASK_DEFINITION,
                new RemoteStartTransactionWithProfileParams(
                        params, createChargingProfile(params.getChargingProfilePk(), chargingProfileRepository)),
                caller);
    }

    public static class RemoteStartTransactionWithProfileParams extends RemoteStartTransactionParams {
        private final ocpp.cp._2015._10.@Nullable ChargingProfile chargingProfile;

        public RemoteStartTransactionWithProfileParams(
                RemoteStartTransactionParams params, ocpp.cp._2015._10.@Nullable ChargingProfile chargingProfile) {
            super();
            this.setIdTag(params.getIdTag());
            this.setConnectorId(params.getConnectorId());
            this.chargingProfile = chargingProfile;
        }

        @Override
        public @Nullable Integer getChargingProfilePk() {
            if (chargingProfile == null) {
                return null;
            }
            return chargingProfile.getChargingProfileId();
        }
    }

    private static @Nullable ChargingProfile createChargingProfile(
            @Nullable Integer chargingProfilePk, ChargingProfileRepository chargingProfileRepository) {
        if (chargingProfilePk == null) {
            return null;
        }
        de.rwth.idsg.steve.repository.dto.ChargingProfile.Details details = chargingProfileRepository
                .getDetails(chargingProfilePk)
                .orElseThrow(() ->
                        new SteveException.BadRequest("ChargingProfile with PK " + chargingProfilePk + " not found"));
        ocpp.cp._2015._10.ChargingProfile chargingProfile = SetChargingProfileTask.mapToOcpp(details, null);
        if (chargingProfile.getChargingProfilePurpose() != ChargingProfilePurposeType.TX_PROFILE) {
            throw new SteveException.BadRequest("ChargingProfilePurposeType is not TX_PROFILE");
        }
        return chargingProfile;
    }
}

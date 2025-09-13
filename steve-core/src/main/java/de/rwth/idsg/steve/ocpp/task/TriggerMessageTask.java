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
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import ocpp.cp._2015._10.MessageTrigger;
import ocpp.cp._2015._10.TriggerMessageResponse;

import java.util.Map;

public class TriggerMessageTask extends CommunicationTask<TriggerMessageParams, String> {

    private static final TaskDefinition<TriggerMessageParams, String> TASK_DEFINITION =
            TaskDefinition.<TriggerMessageParams, String>builder()
                    .versionHandlers(Map.of(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.TriggerMessageRequest()
                                            .withConnectorId(task.getParams().getConnectorId())
                                            .withRequestedMessage(MessageTrigger.fromValue(task.getParams()
                                                    .getTriggerMessage()
                                                    .value())),
                                    (TriggerMessageResponse r) -> r.getStatus().value())))
                    .build();

    public TriggerMessageTask(TriggerMessageParams params) {
        super(params, TASK_DEFINITION);
    }

    public TriggerMessageTask(TriggerMessageParams params, String caller) {
        super(params, caller, TASK_DEFINITION);
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

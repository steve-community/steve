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
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;

public class GetLocalListVersionTask extends CommunicationTask<MultipleChargePointSelect, String> {

    private static final TaskDefinition<MultipleChargePointSelect, String> TASK_DEFINITION =
            TaskDefinition.<MultipleChargePointSelect, String>builder()
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2012._06.GetLocalListVersionRequest(),
                                    (ocpp.cp._2012._06.GetLocalListVersionResponse r) ->
                                            String.valueOf(r.getListVersion())))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.GetLocalListVersionRequest(),
                                    (ocpp.cp._2015._10.GetLocalListVersionResponse r) ->
                                            String.valueOf(r.getListVersion())))
                    .build();

    public GetLocalListVersionTask(MultipleChargePointSelect params) {
        super(TASK_DEFINITION, params);
    }

    public GetLocalListVersionTask(MultipleChargePointSelect params, String caller) {
        super(TASK_DEFINITION, params, caller);
    }
}

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
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toOffsetDateTime;

public class GetDiagnosticsTask extends CommunicationTask<GetDiagnosticsParams, String> {

    private static final TaskDefinition<GetDiagnosticsParams, String> TASK_DEFINITION =
            TaskDefinition.<GetDiagnosticsParams, String>builder()
                    .versionHandler(
                            OcppVersion.V_12,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2010._08.GetDiagnosticsRequest()
                                            .withLocation(task.getParams().getLocation())
                                            .withRetries(task.getParams().getRetries())
                                            .withRetryInterval(task.getParams().getRetryInterval())
                                            .withStartTime(toOffsetDateTime(
                                                    task.getParams().getStart()))
                                            .withStopTime(toOffsetDateTime(
                                                    task.getParams().getStop())),
                                    (ocpp.cp._2010._08.GetDiagnosticsResponse r) -> r.getFileName()))
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2012._06.GetDiagnosticsRequest()
                                            .withLocation(task.getParams().getLocation())
                                            .withRetries(task.getParams().getRetries())
                                            .withRetryInterval(task.getParams().getRetryInterval())
                                            .withStartTime(toOffsetDateTime(
                                                    task.getParams().getStart()))
                                            .withStopTime(toOffsetDateTime(
                                                    task.getParams().getStop())),
                                    (ocpp.cp._2012._06.GetDiagnosticsResponse r) -> r.getFileName()))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.GetDiagnosticsRequest()
                                            .withLocation(task.getParams().getLocation())
                                            .withRetries(task.getParams().getRetries())
                                            .withRetryInterval(task.getParams().getRetryInterval())
                                            .withStartTime(toOffsetDateTime(
                                                    task.getParams().getStart()))
                                            .withStopTime(toOffsetDateTime(
                                                    task.getParams().getStop())),
                                    (ocpp.cp._2015._10.GetDiagnosticsResponse r) -> r.getFileName()))
                    .build();

    public GetDiagnosticsTask(GetDiagnosticsParams params) {
        super(TASK_DEFINITION, params);
    }

    public GetDiagnosticsTask(GetDiagnosticsParams params, String caller) {
        super(TASK_DEFINITION, params, caller);
    }
}

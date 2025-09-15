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
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DataTransferTask extends CommunicationTask<DataTransferParams, DataTransferTask.ResponseWrapper> {

    @Getter
    @RequiredArgsConstructor
    public static class ResponseWrapper {
        private final String status;
        private final String data;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(status);
            if (data != null) {
                builder.append(" / Data: ").append(data);
            }
            return builder.toString();
        }
    }

    private static final TaskDefinition<DataTransferParams, ResponseWrapper> TASK_DEFINITION =
            TaskDefinition.<DataTransferParams, ResponseWrapper>builder()
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2012._06.DataTransferRequest()
                                            .withData(task.getParams().getData())
                                            .withMessageId(task.getParams().getMessageId())
                                            .withVendorId(task.getParams().getVendorId()),
                                    (ocpp.cp._2012._06.DataTransferResponse r) ->
                                            new ResponseWrapper(r.getStatus().value(), r.getData())))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> new ocpp.cp._2015._10.DataTransferRequest()
                                            .withData(task.getParams().getData())
                                            .withMessageId(task.getParams().getMessageId())
                                            .withVendorId(task.getParams().getVendorId()),
                                    (ocpp.cp._2015._10.DataTransferResponse r) ->
                                            new ResponseWrapper(r.getStatus().value(), r.getData())))
                    .build();

    public DataTransferTask(DataTransferParams params) {
        super(TASK_DEFINITION, params);
    }

    public DataTransferTask(DataTransferParams params, String caller) {
        super(TASK_DEFINITION, params, caller);
    }
}

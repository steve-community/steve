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

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class GetConfigurationTask
        extends CommunicationTask<GetConfigurationParams, GetConfigurationTask.ResponseWrapper> {

    private static final Joiner JOINER = Joiner.on(", ");

    private static final TaskDefinition<GetConfigurationParams, ResponseWrapper> TASK_DEFINITION =
            TaskDefinition.<GetConfigurationParams, ResponseWrapper>builder()
                    .versionHandlers(Map.of(
                            OcppVersion.V_15,
                                    new OcppVersionHandler<>(
                                            task -> new ocpp.cp._2012._06.GetConfigurationRequest()
                                                    .withKey(task.getParams().getAllKeys()),
                                            (ocpp.cp._2012._06.GetConfigurationResponse r) -> {
                                                List<KeyValue> keyValues = r.getConfigurationKey().stream()
                                                        .map(k ->
                                                                new KeyValue(k.getKey(), k.getValue(), k.isReadonly()))
                                                        .collect(Collectors.toList());
                                                return new ResponseWrapper(keyValues, r.getUnknownKey());
                                            }),
                            OcppVersion.V_16,
                                    new OcppVersionHandler<>(
                                            task -> new ocpp.cp._2015._10.GetConfigurationRequest()
                                                    .withKey(task.getParams().getAllKeys()),
                                            (ocpp.cp._2015._10.GetConfigurationResponse r) -> {
                                                List<KeyValue> keyValues = r.getConfigurationKey().stream()
                                                        .map(k ->
                                                                new KeyValue(k.getKey(), k.getValue(), k.isReadonly()))
                                                        .collect(Collectors.toList());
                                                return new ResponseWrapper(keyValues, r.getUnknownKey());
                                            })))
                    .build();

    public GetConfigurationTask(GetConfigurationParams params) {
        super(params, TASK_DEFINITION);
    }

    public GetConfigurationTask(GetConfigurationParams params, String caller) {
        super(params, caller, TASK_DEFINITION);
    }

    @Override
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, ResponseWrapper response) {
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

    @Getter
    public static class ResponseWrapper {
        private final List<KeyValue> configurationKeys;
        private final String unknownKeys;

        private ResponseWrapper(List<KeyValue> configurationKeys, List<String> unknownKeys) {
            this.configurationKeys = configurationKeys;
            this.unknownKeys = JOINER.join(unknownKeys);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class KeyValue {
        private final String key;
        private final String value;
        private final boolean readonly;
    }
}

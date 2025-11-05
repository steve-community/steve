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

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import ocpp._2022._02.security.GetLog;
import ocpp._2022._02.security.GetLogResponse;
import ocpp._2022._02.security.LogParametersType;

import jakarta.xml.ws.AsyncHandler;

public class GetLogTask extends Ocpp16AndAboveTask<GetLogParams, String> {

    private final int requestId;

    public GetLogTask(GetLogParams params, int requestId) {
        super(params);
        this.requestId = requestId;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public GetLog getOcpp16Request() {
        var logParams = new LogParametersType();
        logParams.setRemoteLocation(params.getLocation());
        logParams.setOldestTimestamp(params.getStart());
        logParams.setLatestTimestamp(params.getStop());

        var request = new GetLog();
        request.setRequestId(requestId);
        request.setLogType(params.getLogType());
        request.setRetries(params.getRetries());
        request.setRetryInterval(params.getRetryInterval());
        request.setLog(logParams);
        return request;
    }

    @Override
    public AsyncHandler<GetLogResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var response = res.get();
                var status = response.getStatus().value();

                String responseMessage = (response.getFilename() == null)
                    ? status
                    : status + " (filename: " + response.getFilename() + ")";

                success(chargeBoxId, responseMessage);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

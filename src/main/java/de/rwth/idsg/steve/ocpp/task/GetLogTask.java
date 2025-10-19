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

import jakarta.xml.ws.AsyncHandler;
import ocpp._2020._03.GetLogRequest;
import ocpp._2020._03.GetLogResponse;
import ocpp._2020._03.LogParametersType;

public class GetLogTask extends Ocpp16AndAboveTask<GetLogParams, String> {

    public GetLogTask(GetLogParams params) {
        super(params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public GetLogRequest getOcpp16Request() {
        var request = new GetLogRequest();
        request.setLogType(GetLogRequest.LogEnumType.valueOf(params.getLogType().toString()));
        request.setRequestId(params.getRequestId());

        var logParams = new LogParametersType();
        logParams.setRemoteLocation(params.getLocation());

        if (params.getOldestTimestamp() != null) {
            logParams.setOldestTimestamp(params.getOldestTimestamp());
        }
        if (params.getLatestTimestamp() != null) {
            logParams.setLatestTimestamp(params.getLatestTimestamp());
        }

        request.setLog(logParams);

        if (params.getRetries() != null) {
            request.setRetries(params.getRetries());
        }
        if (params.getRetryInterval() != null) {
            request.setRetryInterval(params.getRetryInterval());
        }

        return request;
    }

    @Override
    public AsyncHandler<GetLogResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var response = res.get();
                var status = response.getStatus() != null ? response.getStatus().toString() : "Unknown";
                var filename = response.getFilename() != null ? response.getFilename() : "N/A";
                success(chargeBoxId, status + " (filename: " + filename + ")");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

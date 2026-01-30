/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import org.apache.commons.lang3.StringUtils;

import jakarta.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class GetDiagnosticsTask extends CommunicationTask<GetDiagnosticsParams, ocpp.cp._2015._10.GetDiagnosticsResponse> {

    public GetDiagnosticsTask(GetDiagnosticsParams params) {
        super(params);
    }

    @Override
    public OcppCallback<ocpp.cp._2015._10.GetDiagnosticsResponse> defaultCallback() {
        return new DefaultOcppCallback<ocpp.cp._2015._10.GetDiagnosticsResponse>() {
            @Override
            public void success(String chargeBoxId, ocpp.cp._2015._10.GetDiagnosticsResponse response) {
                addNewResponse(chargeBoxId, "filename: " + StringUtils.defaultString(response.getFileName()));
            }
        };
    }

    @Override
    public ocpp.cp._2010._08.GetDiagnosticsRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(params.getStart())
                .withStopTime(params.getStop());
    }

    @Override
    public ocpp.cp._2012._06.GetDiagnosticsRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(params.getStart())
                .withStopTime(params.getStop());
    }

    @Override
    public ocpp.cp._2015._10.GetDiagnosticsRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(params.getStart())
                .withStopTime(params.getStop());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.GetDiagnosticsResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                var data = new ocpp.cp._2015._10.GetDiagnosticsResponse().withFileName(res.get().getFileName());
                success(chargeBoxId, data);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.GetDiagnosticsResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                var data = new ocpp.cp._2015._10.GetDiagnosticsResponse().withFileName(res.get().getFileName());
                success(chargeBoxId, data);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.GetDiagnosticsResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

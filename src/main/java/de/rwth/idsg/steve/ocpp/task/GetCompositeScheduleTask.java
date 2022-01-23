/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import ocpp.cp._2015._10.GetCompositeScheduleRequest;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import ocpp.cp._2015._10.GetCompositeScheduleStatus;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
public class GetCompositeScheduleTask extends Ocpp16AndAboveTask<GetCompositeScheduleParams, GetCompositeScheduleResponse> {

    public GetCompositeScheduleTask(OcppVersion ocppVersion,
                                    GetCompositeScheduleParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<GetCompositeScheduleResponse> defaultCallback() {
        return new DefaultOcppCallback<GetCompositeScheduleResponse>() {

            @Override
            public void success(String chargeBoxId, GetCompositeScheduleResponse response) {
                addNewResponse(chargeBoxId, response.getStatus().value());

                if (response.getStatus() == GetCompositeScheduleStatus.ACCEPTED) {
                    RequestResult result = getResultMap().get(chargeBoxId);
                    result.setDetails(response);
                }
            }
        };
    }

    @Override
    public GetCompositeScheduleRequest getOcpp16Request() {
        return new GetCompositeScheduleRequest()
                .withConnectorId(params.getConnectorId())
                .withDuration(params.getDurationInSeconds())
                .withChargingRateUnit(params.getChargingRateUnit());
    }

    @Override
    public AsyncHandler<GetCompositeScheduleResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

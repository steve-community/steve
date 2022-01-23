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

import de.rwth.idsg.steve.ocpp.Ocpp15AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.DataTransferResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class DataTransferTask extends Ocpp15AndAboveTask<DataTransferParams, DataTransferTask.ResponseWrapper> {

    public DataTransferTask(OcppVersion ocppVersion, DataTransferParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new DefaultOcppCallback<ResponseWrapper>() {
            @Override
            public void success(String chargeBoxId, ResponseWrapper response) {
                String status = response.getStatus();
                String data = response.getData();

                StringBuilder builder = new StringBuilder(status);
                if (data != null) {
                    builder.append(" / Data: ").append(data);
                }
                addNewResponse(chargeBoxId, builder.toString());
            }
        };
    }

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    @Override
    public ocpp.cp._2012._06.DataTransferRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.DataTransferRequest()
                .withData(params.getData())
                .withMessageId(params.getMessageId())
                .withVendorId(params.getVendorId());
    }

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    @Override
    public ocpp.cp._2015._10.DataTransferRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.DataTransferRequest()
                .withData(params.getData())
                .withMessageId(params.getMessageId())
                .withVendorId(params.getVendorId());
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.DataTransferResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                DataTransferResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(), response.getData()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.DataTransferResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ocpp.cp._2015._10.DataTransferResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(), response.getData()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Getter
    @RequiredArgsConstructor
    public static class ResponseWrapper {
        private final String status;
        private final String data;
    }
}

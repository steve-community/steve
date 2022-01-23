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

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;

import javax.xml.ws.AsyncHandler;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class UpdateFirmwareTask extends CommunicationTask<UpdateFirmwareParams, String> {

    public UpdateFirmwareTask(OcppVersion ocppVersion, UpdateFirmwareParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.UpdateFirmwareRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    @Override
    public ocpp.cp._2012._06.UpdateFirmwareRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    @Override
    public ocpp.cp._2015._10.UpdateFirmwareRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.UpdateFirmwareResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, "OK");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.UpdateFirmwareResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, "OK");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.UpdateFirmwareResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, "OK");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

}

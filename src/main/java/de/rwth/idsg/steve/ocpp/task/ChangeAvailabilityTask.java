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
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class ChangeAvailabilityTask extends CommunicationTask<ChangeAvailabilityParams, String> {

    public ChangeAvailabilityTask(OcppVersion ocppVersion, ChangeAvailabilityParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.ChangeAvailabilityRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(ocpp.cp._2010._08.AvailabilityType.fromValue(params.getAvailType().value()));
    }

    @Override
    public ocpp.cp._2012._06.ChangeAvailabilityRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(ocpp.cp._2012._06.AvailabilityType.fromValue(params.getAvailType().value()));
    }

    @Override
    public ocpp.cp._2015._10.ChangeAvailabilityRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(ocpp.cp._2015._10.AvailabilityType.fromValue(params.getAvailType().value()));
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ChangeAvailabilityResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ChangeAvailabilityResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ChangeAvailabilityResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

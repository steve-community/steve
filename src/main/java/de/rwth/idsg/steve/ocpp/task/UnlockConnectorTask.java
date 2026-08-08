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
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import ocpp.cp._2015._10.UnlockStatus;

import jakarta.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class UnlockConnectorTask extends CommunicationTask<UnlockConnectorParams, UnlockStatus> {

    public UnlockConnectorTask(UnlockConnectorParams params) {
        super(params);
    }

    @Override
    public OcppCallback<UnlockStatus> defaultCallback() {
        return new DefaultOcppCallback<UnlockStatus>() {
            @Override
            public void success(String chargeBoxId, UnlockStatus response) {
                addNewResponse(chargeBoxId, response.value());
            }
        };
    }

    @Override
    public ocpp.cp._2010._08.UnlockConnectorRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2012._06.UnlockConnectorRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2015._10.UnlockConnectorRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.UnlockConnectorResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, UnlockStatus.fromValue(res.get().getStatus().value()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.UnlockConnectorResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, UnlockStatus.fromValue(res.get().getStatus().value()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.UnlockConnectorResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

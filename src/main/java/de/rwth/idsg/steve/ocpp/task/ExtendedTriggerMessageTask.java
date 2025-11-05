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
import de.rwth.idsg.steve.web.dto.ocpp.ExtendedTriggerMessageParams;
import ocpp._2022._02.security.ExtendedTriggerMessage;
import ocpp._2022._02.security.ExtendedTriggerMessageResponse;

import jakarta.xml.ws.AsyncHandler;

public class ExtendedTriggerMessageTask extends Ocpp16AndAboveTask<ExtendedTriggerMessageParams, String> {

    public ExtendedTriggerMessageTask(ExtendedTriggerMessageParams params) {
        super(params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ExtendedTriggerMessage getOcpp16Request() {
        var request = new ExtendedTriggerMessage();
        request.setRequestedMessage(params.getTriggerMessage());
        request.setConnectorId(params.getConnectorId());
        return request;
    }

    @Override
    public AsyncHandler<ExtendedTriggerMessageResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

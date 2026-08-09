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

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import ocpp.cp._2015._10.MessageTrigger;
import ocpp.cp._2015._10.TriggerMessageStatus;

import jakarta.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
public class TriggerMessageTask extends Ocpp16AndAboveTask<TriggerMessageParams, TriggerMessageStatus> {

    public TriggerMessageTask(TriggerMessageParams params) {
        super(params);
    }

    @Override
    public OcppCallback<TriggerMessageStatus> defaultCallback() {
        return new DefaultOcppCallback<TriggerMessageStatus>() {
            @Override
            public void success(String chargeBoxId, TriggerMessageStatus response) {
                addNewResponse(chargeBoxId, response.value());
            }
        };
    }

    @Override
    public ocpp.cp._2015._10.TriggerMessageRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.TriggerMessageRequest()
                .withConnectorId(params.getConnectorId())
                .withRequestedMessage(MessageTrigger.fromValue(params.getTriggerMessage().value()));
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.TriggerMessageResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

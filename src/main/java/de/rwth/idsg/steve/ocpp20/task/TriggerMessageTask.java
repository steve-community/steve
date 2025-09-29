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
package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.TriggerMessageRequest;
import de.rwth.idsg.steve.ocpp20.model.TriggerMessageResponse;
import de.rwth.idsg.steve.ocpp20.model.MessageTriggerEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class TriggerMessageTask extends Ocpp20Task<TriggerMessageRequest, TriggerMessageResponse> {

    private final MessageTriggerEnum requestedMessage;
    private final Integer evseId;
    private final Integer connectorId;

    public TriggerMessageTask(List<String> chargeBoxIds, MessageTriggerEnum requestedMessage) {
        this(chargeBoxIds, requestedMessage, null, null);
    }

    public TriggerMessageTask(List<String> chargeBoxIds, MessageTriggerEnum requestedMessage, Integer evseId) {
        this(chargeBoxIds, requestedMessage, evseId, null);
    }

    public TriggerMessageTask(List<String> chargeBoxIds, MessageTriggerEnum requestedMessage, Integer evseId, Integer connectorId) {
        super("TriggerMessage", chargeBoxIds);
        this.requestedMessage = requestedMessage;
        this.evseId = evseId;
        this.connectorId = connectorId;
    }

    @Override
    public TriggerMessageRequest createRequest() {
        TriggerMessageRequest request = new TriggerMessageRequest();
        request.setRequestedMessage(requestedMessage);

        if (evseId != null) {
            de.rwth.idsg.steve.ocpp20.model.EVSE evse = new de.rwth.idsg.steve.ocpp20.model.EVSE();
            evse.setId(evseId);
            if (connectorId != null) {
                evse.setConnectorId(connectorId);
            }
            request.setEvse(evse);
        }

        return request;
    }

    @Override
    public Class<TriggerMessageResponse> getResponseClass() {
        return TriggerMessageResponse.class;
    }
}
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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;

import java.util.UUID;
import java.util.function.Function;

public record InvocationContext(
        AbstractWebSocketEndpoint endpoint,
        TypeStore typeStore,
        Function<CommunicationTask<?, ?>, RequestType> request
) {
    public CommunicationContext toCommunicationContext(String chargeBoxId, CommunicationTask<?, ?> task) {
        var context = new CommunicationContext(endpoint().getSession(chargeBoxId), chargeBoxId);

        var requestType = request().apply(task);

        var pair = typeStore().findActionResponse(requestType);
        if (pair == null) {
            throw new SteveException("Action name is not found");
        }

        var call = new OcppJsonCall();
        call.setMessageId(UUID.randomUUID().toString());
        call.setPayload(requestType);
        call.setAction(pair.getAction());
        context.setOutgoingMessage(call);

        var frc = new FutureResponseContext(task, pair.getResponseClass());
        context.setFutureResponseContext(frc);

        return context;
    }
}

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
package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
public abstract class AbstractCallHandler implements Consumer<CommunicationContext> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void accept(CommunicationContext context) {
        OcppJsonCall call = (OcppJsonCall) context.getIncomingMessage();
        String messageId = call.getMessageId();

        ResponseType response;
        try {
            response = dispatch(call.getPayload(), context.getChargeBoxId());
        } catch (Exception e) {
            log.error("Exception occurred", e);
            context.setOutgoingMessage(ErrorFactory.payloadProcessingError(messageId, e.getMessage()));
            return;
        }

        OcppJsonResult result = new OcppJsonResult();
        result.setPayload(response);
        result.setMessageId(messageId);
        context.setOutgoingMessage(result);
    }

    protected abstract ResponseType dispatch(RequestType params, String chargeBoxId);
}

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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.WebSocketLogger;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * This class should remain stateless.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Slf4j
public enum Sender implements Consumer<CommunicationContext> {
    INSTANCE;

    @Override
    public void accept(CommunicationContext context) {
        String outgoingString = context.getOutgoingString();
        String chargeBoxId = context.getChargeBoxId();
        WebSocketSession session = context.getSession();

        WebSocketLogger.sending(chargeBoxId, session, outgoingString);

        TextMessage out = new TextMessage(outgoingString);
        try {
            session.sendMessage(out);
        } catch (IOException e) {

            // Do NOT swallow exceptions for outgoing CALLs. For others just log.
            if (context.getOutgoingMessage() instanceof OcppJsonCall) {
                throw new SteveException(e.getMessage());
            } else {
                log.error("Could not send the outgoing message", e);
            }
        }
    }
}

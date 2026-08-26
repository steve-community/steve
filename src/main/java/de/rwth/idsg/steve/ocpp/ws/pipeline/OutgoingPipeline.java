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
package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.WebSocketLogger;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.repository.TaskStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.03.2015
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutgoingPipeline {

    private final TaskStore taskStore;
    private final SessionContextStoreHolder sessionContextStoreHolder;
    private final FutureResponseContextStore futureResponseContextStore;

    public void accept(CommunicationContext.Out outMsg) {
        var version = outMsg.protocol().getVersion();
        var chargeBoxId = outMsg.chargeBoxId();
        var webSocketSessionId = outMsg.webSocketSessionId();

        // Retrieve session by id
        var sessionStore = sessionContextStoreHolder.getOrCreate(version);
        var session = sessionStore.getSession(chargeBoxId, webSocketSessionId);

        sendOut(outMsg, session);
    }

    /**
     * For outgoing CALLs, triggered by the user.
     * Uses a store-before-send strategy to close response-correlation races.
     * If transport sending fails, the stored context is rolled back immediately.
     */
    public void accept(CommunicationContext.OutCall outWithCall) {
        var version = outWithCall.protocol().getVersion();

        // Pick a session to send
        var sessionStore = sessionContextStoreHolder.getOrCreate(version);
        var session = sessionStore.getSession(outWithCall.chargeBoxId());

        // Reconstruct CommunicationTask from its id
        var typeStore = TypeStore.getTypeStore(version);
        var task = taskStore.get(outWithCall.taskId());

        // Construct a response context before send
        var responseClass = typeStore.findResponseClass(outWithCall.ocppAction());
        var frc = new FutureResponseContext(task, responseClass);

        // 1. Store the response context for later lookup.
        futureResponseContextStore.add(
            session.getId(),
            outWithCall.ocppMessageId(),
            frc
        );

        // 2. Send the payload via WebSocket
        try {
            if (!sendOutCall(outWithCall, session)) {
                poll(outWithCall, session);
            }
        } catch (Exception e) {
            poll(outWithCall, session);
            throw e;
        }
    }

    /**
     * 3. In case of failure, rollback aka remove the FutureResponseContext
     */
    private void poll(CommunicationContext.OutCall outWithCall, WebSocketSession session) {
        futureResponseContextStore.poll(session.getId(), outWithCall.ocppMessageId());
    }

    // -------------------------------------------------------------------------
    // Actual sending over WebSocket
    // -------------------------------------------------------------------------

    private static void sendOut(CommunicationContext.Out outMsg,  @Nullable WebSocketSession session) {
        String outgoingString = outMsg.ocppPayload();
        String chargeBoxId = outMsg.chargeBoxId();
        String webSocketSessionId = outMsg.webSocketSessionId();

        // https://github.com/steve-community/steve/issues/1914
        if (session == null || !session.isOpen()) {
            WebSocketLogger.willNotSend(chargeBoxId, webSocketSessionId, outgoingString);
            return;
        }

        WebSocketLogger.sending(chargeBoxId, session, outgoingString);
        TextMessage out = new TextMessage(outgoingString);
        try {
            session.sendMessage(out);
        } catch (IOException e) {
            // Just log. We cannot do anything else when we could not reply with a response.
            log.error("Could not send the outgoing response message", e);
        }
    }

    /**
     * Return value is used by callers to decide whether they must rollback any pre-send bookkeeping.
     * Outgoing CALL failures are surfaced as exceptions to avoid silently keeping invalid correlation state.
     *
     * @return whether the message was actually sent or not
     */
    private static boolean sendOutCall(CommunicationContext.OutCall outMsg, WebSocketSession session) {
        String outgoingString = outMsg.ocppPayload();
        String chargeBoxId = outMsg.chargeBoxId();
        String webSocketSessionId = session.getId();

        // https://github.com/steve-community/steve/issues/1914
        if (!session.isOpen()) {
            WebSocketLogger.willNotSend(chargeBoxId, webSocketSessionId, outgoingString);
            return false;
        }

        WebSocketLogger.sending(chargeBoxId, session, outgoingString);
        TextMessage out = new TextMessage(outgoingString);
        try {
            session.sendMessage(out);
            return true;
        } catch (IOException e) {
            // Do NOT swallow exceptions for outgoing CALLs.
            throw new SteveException("OCPP CALL failed", e);
        }
    }
}

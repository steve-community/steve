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

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.repository.TaskStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.03.2015
 */
@RequiredArgsConstructor
@Component
public class OutgoingCallPipeline {

    private final Sender sender;
    private final TaskStore taskStore;
    private final SessionContextStoreHolder sessionContextStoreHolder;
    private final FutureResponseContextStore futureResponseContextStore;

    /**
     * Uses a store-before-send strategy to close response-correlation races.
     * If transport sending fails, the stored context is rolled back immediately.
     */
    public void accept(CommunicationContext.OutCall outWithCall) {
        // Pick a session to send
        var sessionStore = sessionContextStoreHolder.getOrCreate(outWithCall.protocol().getVersion());
        var session = sessionStore.getSession(outWithCall.chargeBoxId());

        // Reconstruct CommunicationTask from its id
        var typeStore = TypeStore.getTypeStore(outWithCall.protocol().getVersion());
        var task = taskStore.get(outWithCall.taskId());

        // Construct a response context before send
        var responseClass = typeStore.findResponseClass(outWithCall.action());
        var frc = new FutureResponseContext(task, responseClass);

        // 1. Store the response context for later lookup.
        futureResponseContextStore.add(
            session.getId(),
            outWithCall.ocppMessageId(),
            frc
        );

        // 2. Send the payload via WebSocket
        try {
            if (!sender.accept(outWithCall, session)) {
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
}

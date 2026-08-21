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

import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    private final FutureResponseContextStore store;

    /**
     * Uses a store-before-send strategy to close response-correlation races.
     * If transport sending fails, the stored context is rolled back immediately.
     */
    public void accept(CommunicationContext.OutCall outWithCall) {
        // 1. Create the payload to send
        String outMsg = Serializer.INSTANCE.accept(outWithCall.call());

        // 2. Store the response context for later lookup.
        store.add(
            outWithCall.route().webSocketSessionId(),
            outWithCall.call().getMessageId(),
            outWithCall.frc()
        );

        // 3. Send the payload via WebSocket
        try {
            var out = new CommunicationContext.Out(
                outWithCall.route(),
                outMsg,
                outWithCall.call().getMessageType()
            );

            if (!sender.accept(out)) {
                poll(outWithCall);
            }
        } catch (Exception e) {
            poll(outWithCall);
            throw e;
        }
    }

    private void poll(CommunicationContext.OutCall outWithCall) {
        store.poll(outWithCall.route().webSocketSessionId(), outWithCall.call().getMessageId());
    }
}

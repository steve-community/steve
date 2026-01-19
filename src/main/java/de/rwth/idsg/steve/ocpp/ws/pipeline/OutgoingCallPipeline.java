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
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.03.2015
 */
@RequiredArgsConstructor
@Component
public class OutgoingCallPipeline implements Consumer<CommunicationContext> {

    private final FutureResponseContextStore store;

    @Override
    public void accept(CommunicationContext ctx) {
        // 1. Create the payload to send
        Serializer.INSTANCE.accept(ctx);

        // 2. Send the payload via WebSocket
        Sender.INSTANCE.accept(ctx);

        // 3. All went well, and the call is sent. Store the response context for later lookup.
        store.add(
            ctx.getSession(),
            ctx.getOutgoingMessage().getMessageId(),
            ctx.getFutureResponseContext()
        );
    }
}

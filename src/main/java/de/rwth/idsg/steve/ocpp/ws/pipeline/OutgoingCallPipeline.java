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

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * For outgoing CALLs, triggered by the user.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.03.2015
 */
@Component
@Slf4j
public class OutgoingCallPipeline implements Consumer<CommunicationContext> {

    private final Consumer<CommunicationContext> chainedConsumers;

    @Autowired
    public OutgoingCallPipeline(FutureResponseContextStore store) {
        chainedConsumers = OutgoingCallPipeline.start(Serializer.INSTANCE)
                                               .andThen(Sender.INSTANCE)
                                               .andThen(saveInStore(store));
    }

    @Override
    public void accept(CommunicationContext ctx) {
        chainedConsumers.accept(ctx);
    }

    private static Consumer<CommunicationContext> saveInStore(FutureResponseContextStore store) {
        return context -> {
            // All went well, and the call is sent. Store the response context for later lookup.
            store.add(context.getSession(),
                      context.getOutgoingMessage().getMessageId(),
                      context.getFutureResponseContext());
        };
    }

    private static Consumer<CommunicationContext> start(Consumer<CommunicationContext> starter) {
        return starter;
    }

}

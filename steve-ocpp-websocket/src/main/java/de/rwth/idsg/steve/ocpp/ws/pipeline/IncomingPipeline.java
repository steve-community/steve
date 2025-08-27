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
package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * For all incoming message types.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class IncomingPipeline implements Consumer<CommunicationContext> {

    private final Serializer serializer;
    private final Deserializer deserializer;
    private final Sender sender;
    private final AbstractCallHandler handler;

    @Override
    public void accept(CommunicationContext context) {
        deserializer.accept(context);

        // When the incoming could not be deserialized
        if (context.isSetOutgoingError()) {
            send(context);
            return;
        }

        var msg = context.getIncomingMessage();
        switch (msg) {
            case OcppJsonCall ocppJsonCall -> {
                handler.accept(context);
                send(context);
            }
            case OcppJsonResult resultMsg -> context.getResultHandler().accept(resultMsg);
            case OcppJsonError errorMsg -> context.getErrorHandler().accept(errorMsg);
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
    }

    private void send(CommunicationContext context) {
        serializer.accept(context);
        sender.accept(context);
    }
}

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

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.ws.Response;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    private final Serializer serializer = Serializer.INSTANCE;
    private final Sender sender = Sender.INSTANCE;

    private final Deserializer deserializer;
    private final OcppCallHandler handler;

    @Override
    public void accept(CommunicationContext context) {
        try {
            deserializer.accept(context);
        } catch (SteveException e) {
            // do not let OcppCallbacks hang. try to inform them when the response from the station cannot be parsed.
            var frc = context.getFutureResponseContext();
            if (frc != null) {
                frc.getTask().failed(context.getChargeBoxId(), e);
            }
            throw e;
        }

        // When the incoming could not be deserialized
        if (context.isSetOutgoingError()) {
            serializer.accept(context);
            sender.accept(context);
            return;
        }

        switch (context.getIncomingMessage()) {
            case OcppJsonCall call -> processCall(context, call);
            case OcppJsonResult result -> processResult(context, result);
            case OcppJsonError error -> processError(context, error);
            default -> log.warn("Unexpected value: {}", context.getIncomingMessage());
        }
    }

    private void processCall(CommunicationContext context, OcppJsonCall call) {
        handler.accept(context);
        serializer.accept(context);
        sender.accept(context);
    }

    @SuppressWarnings("unchecked")
    private void processResult(CommunicationContext context, OcppJsonResult result) {
        context.getFutureResponseContext()
            .getTask()
            .getHandler(context.getChargeBoxId())
            .handleResponse(new DummyResponse(result.getPayload()));
    }

    private void processError(CommunicationContext context, OcppJsonError error) {
        context.getFutureResponseContext()
            .getTask()
            .success(context.getChargeBoxId(), error);
    }

    private record DummyResponse(ResponseType payload) implements Response<ResponseType> {
        @Override
        public Map<String, Object> getContext() {
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public ResponseType get() {
            return payload;
        }

        @Override
        public ResponseType get(long timeout, TimeUnit unit) {
            return payload;
        }
    }
}

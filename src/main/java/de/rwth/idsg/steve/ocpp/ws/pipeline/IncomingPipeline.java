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
import de.rwth.idsg.steve.config.MessagingConfiguration;
import de.rwth.idsg.steve.messaging.Messaging;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import jakarta.xml.ws.Response;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * For all incoming message types.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2015
 */
@Slf4j
@Component
public class IncomingPipeline implements Messaging.In.Consumer {

    private final Serializer serializer = Serializer.INSTANCE;

    private final Messaging.Out.Producer outProducer;
    private final Deserializer deserializer;
    private final Map<OcppVersion, OcppCallHandler> handlerMap = new EnumMap<>(OcppVersion.class);

    @Autowired
    public IncomingPipeline(Messaging.Out.Producer outProducer,
                            Deserializer deserializer,
                            List<OcppCallHandler> handlers) {
        this.outProducer = outProducer;
        this.deserializer = deserializer;
        for (OcppCallHandler handler : handlers) {
            handlerMap.put(handler.getVersion(), handler);
        }
    }

    @ServiceActivator(
        inputChannel = MessagingConfiguration.IN_CHANNEL,
        poller = @Poller(
            fixedDelay = MessagingConfiguration.POLL_INTERVAL_MILLIS,
            maxMessagesPerPoll = "-1",
            receiveTimeout = "0"
        )
    )
    @Override
    public void processIn(Message<CommunicationContext.In> message) {
        var inMsg = message.getPayload();
        CommunicationContext.DeserializationResult inMsgData;
        try {
            inMsgData = deserializer.accept(inMsg);

        } catch (CommunicationContext.JsonCallParseException e) {
            var parseError = e.getParseError();
            var parseErrorStr = serializer.accept(parseError);
            sendOut(message, parseErrorStr);
            return;

        } catch (CommunicationContext.JsonResponseInvalidException e) {
            var frc = e.getFrc();
            if (frc != null) {
                frc.getTask().failed(inMsg.chargeBoxId(), e);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new SteveException("Deserialization of incoming string failed: %s", inMsg.ocppPayload(), e);
        }

        switch (inMsgData) {
            case CommunicationContext.InCall call -> processCall(message, call);
            case CommunicationContext.InResult result -> processResult(result);
            case CommunicationContext.InError error -> processError(error);
        }
    }

    private void processCall(Message<CommunicationContext.In> message, CommunicationContext.InCall data) {
        var version = data.in().protocol().getVersion();

        var handler = handlerMap.get(version);
        if (handler == null) {
            // should not happen, means impl or config error
            throw new SteveException("Unknown protocol version: " + version);
        }

        var response = handler.accept(data);
        var responseStr = serializer.accept(response);
        sendOut(message, responseStr);
    }

    private void sendOut(Message<CommunicationContext.In> inMsg, String ocppPayload) {
        var out = CommunicationContext.outFrom(inMsg.getPayload(), ocppPayload);
        var outMsg = MessageBuilder.withPayload(out).copyHeaders(inMsg.getHeaders()).build();
        outProducer.send(outMsg);
    }

    @SuppressWarnings("unchecked")
    private void processResult(CommunicationContext.InResult data) {
        data.frc()
            .getTask()
            .getHandler(data.in().chargeBoxId())
            .handleResponse(new DummyResponse(data.result().getPayload()));
    }

    private void processError(CommunicationContext.InError data) {
        data.frc()
            .getTask()
            .success(data.in().chargeBoxId(), data.error());
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

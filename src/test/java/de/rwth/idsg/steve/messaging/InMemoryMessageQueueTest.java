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
package de.rwth.idsg.steve.messaging;

import de.rwth.idsg.steve.config.MessagingConfiguration;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingPipeline;
import de.rwth.idsg.steve.repository.TaskStore;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InMemoryMessageQueueTest {

    private final MessagingConfiguration configuration = new MessagingConfiguration();

    @Test
    public void producersEnqueueTheirMessages() {
        var inChannel = configuration.ocppInChannel();
        var outChannel = configuration.ocppOutChannel();
        var outCallChannel = configuration.ocppOutCallChannel();
        var in = inMessage();
        var out = outMessage();
        var outCall = outCallMessage();

        configuration.inProducer(inChannel).send(in);
        configuration.outProducer(outChannel).send(out);
        configuration.outCallProducer(outCallChannel).send(outCall);

        assertSame(in, inChannel.receive(0));
        assertSame(out, outChannel.receive(0));
        assertSame(outCall, outCallChannel.receive(0));
    }

    @Test
    public void producersFailWhenTheirQueueRemainsFull() {
        var inChannel = new QueueChannel(1);
        var outChannel = new QueueChannel(1);
        var outCallChannel = new QueueChannel(1);
        var inProducer = configuration.inProducer(inChannel);
        var outProducer = configuration.outProducer(outChannel);
        var outCallProducer = configuration.outCallProducer(outCallChannel);
        var in = inMessage();
        var out = outMessage();
        var outCall = outCallMessage();

        inProducer.send(in);
        outProducer.send(out);
        outCallProducer.send(outCall);

        assertThrows(MessageDeliveryException.class, () -> inProducer.send(in));
        assertThrows(MessageDeliveryException.class, () -> outProducer.send(out));
        assertThrows(MessageDeliveryException.class, () -> outCallProducer.send(outCall));
    }

    @Test
    public void queuesRouteMessagesToTheirConsumers() throws Exception {
        var deserializer = mock(Deserializer.class);
        var sessionContextStoreHolder = mock(SessionContextStoreHolder.class);
        var sessionContextStore = mock(SessionContextStore.class);
        var session = mock(WebSocketSession.class);
        var taskStore = mock(TaskStore.class);
        var task = mock(CommunicationTask.class);
        var futureResponseContextStore = mock(FutureResponseContextStore.class);
        var inboundFutureResponseContext = mock(FutureResponseContext.class);
        var error = mock(OcppJsonError.class);
        var in = inMessage();
        var out = outMessage();
        var outCall = outCallMessage();

        when(deserializer.accept(in.getPayload())).thenReturn(new CommunicationContext.InError(
            in.getPayload(),
            error,
            inboundFutureResponseContext
        ));
        when(inboundFutureResponseContext.getTask()).thenReturn(task);
        when(sessionContextStoreHolder.getOrCreate(OcppProtocol.V_16_JSON.getVersion()))
            .thenReturn(sessionContextStore);
        when(sessionContextStore.getSession("station", "session")).thenReturn(session);
        when(sessionContextStore.getSession("station")).thenReturn(session);
        when(session.getId()).thenReturn("session");
        when(session.isOpen()).thenReturn(true);
        when(taskStore.get(outCall.getPayload().taskUuid())).thenReturn(task);

        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(MessagingConfiguration.class);
            context.registerBean(Deserializer.class, () -> deserializer);
            context.registerBean(SessionContextStoreHolder.class, () -> sessionContextStoreHolder);
            context.registerBean(TaskStore.class, () -> taskStore);
            context.registerBean(FutureResponseContextStore.class, () -> futureResponseContextStore);
            context.register(IncomingPipeline.class, OutgoingPipeline.class);
            context.refresh();

            context.getBean(Messaging.In.Producer.class).send(in);
            context.getBean(Messaging.Out.Producer.class).send(out);
            context.getBean(Messaging.OutCall.Producer.class).send(outCall);

            verify(task, timeout(2_000)).success("station", error);
            verify(session, timeout(2_000).times(2)).sendMessage(any());
            verify(futureResponseContextStore, timeout(2_000)).add(eq("session"), eq("message"), any());
        }
    }

    @Test
    public void incomingPipelinePreservesHeadersOnItsOutgoingMessage() throws Exception {
        var outProducer = mock(Messaging.Out.Producer.class);
        var deserializer = mock(Deserializer.class);
        var in = inMessage();
        var parseError = new OcppJsonError();
        parseError.setMessageId("message");
        parseError.setErrorCode(ErrorCode.FormationViolation);
        when(deserializer.accept(in.getPayload()))
            .thenThrow(new CommunicationContext.JsonCallParseException(parseError));
        var pipeline = new IncomingPipeline(outProducer, deserializer, List.of());

        pipeline.processIn(in);

        var captor = forClass(Message.class);
        verify(outProducer).send(captor.capture());
        assertEquals("testValue", captor.getValue().getHeaders().get("testHeader"));
    }

    @Test
    public void outgoingCallConsumerReportsAsynchronousSendFailureToTask() {
        var taskStore = mock(TaskStore.class);
        var task = mock(CommunicationTask.class);
        var sessionContextStoreHolder = mock(SessionContextStoreHolder.class);
        var sessionContextStore = mock(SessionContextStore.class);
        var session = mock(WebSocketSession.class);
        var futureResponseContextStore = mock(FutureResponseContextStore.class);
        var outCall = outCallMessage();

        when(taskStore.get(outCall.getPayload().taskUuid())).thenReturn(task);
        when(sessionContextStoreHolder.getOrCreate(OcppProtocol.V_16_JSON.getVersion()))
            .thenReturn(sessionContextStore);
        when(sessionContextStore.getSession("station")).thenReturn(session);
        when(session.getId()).thenReturn("session");
        when(session.isOpen()).thenReturn(false);
        var pipeline = new OutgoingPipeline(
            taskStore,
            sessionContextStoreHolder,
            futureResponseContextStore
        );

        assertThrows(RuntimeException.class, () -> pipeline.processOutCall(outCall));

        verify(futureResponseContextStore).poll("session", "message");
        verify(task).failed(eq("station"), any(RuntimeException.class));
    }

    @Test
    public void outgoingCallConsumerStoresCorrelationBeforeSending() throws Exception {
        var taskStore = mock(TaskStore.class);
        var task = mock(CommunicationTask.class);
        var sessionContextStoreHolder = mock(SessionContextStoreHolder.class);
        var sessionContextStore = mock(SessionContextStore.class);
        var session = mock(WebSocketSession.class);
        var futureResponseContextStore = mock(FutureResponseContextStore.class);
        var outCall = outCallMessage();

        when(taskStore.get(outCall.getPayload().taskUuid())).thenReturn(task);
        when(sessionContextStoreHolder.getOrCreate(OcppProtocol.V_16_JSON.getVersion()))
            .thenReturn(sessionContextStore);
        when(sessionContextStore.getSession("station")).thenReturn(session);
        when(session.getId()).thenReturn("session");
        when(session.isOpen()).thenReturn(true);
        var pipeline = new OutgoingPipeline(
            taskStore,
            sessionContextStoreHolder,
            futureResponseContextStore
        );

        pipeline.processOutCall(outCall);

        var inOrder = inOrder(futureResponseContextStore, session);
        inOrder.verify(futureResponseContextStore).add(eq("session"), eq("message"), any());
        inOrder.verify(session).sendMessage(any());
    }

    private static Message<CommunicationContext.In> inMessage() {
        return message(new CommunicationContext.In(
            "station",
            OcppProtocol.V_16_JSON,
            "session",
            System.currentTimeMillis(),
            "in"
        ));
    }

    private static Message<CommunicationContext.Out> outMessage() {
        return message(new CommunicationContext.Out(
            "station",
            OcppProtocol.V_16_JSON,
            "session",
            "out"
        ));
    }

    private static Message<CommunicationContext.OutCall> outCallMessage() {
        return message(new CommunicationContext.OutCall(
            "station",
            OcppProtocol.V_16_JSON,
            UUID.randomUUID(),
            "call",
            "Heartbeat",
            "message"
        ));
    }

    private static <T> Message<T> message(T payload) {
        return MessageBuilder.withPayload(payload)
            .setHeader("testHeader", "testValue")
            .build();
    }
}

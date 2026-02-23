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

import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStoreImpl;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;

import java.util.UUID;

import static de.rwth.idsg.steve.ocpp.ws.data.ErrorCode.FormationViolation;
import static de.rwth.idsg.steve.ocpp.ws.data.ErrorCode.InternalError;
import static de.rwth.idsg.steve.ocpp.ws.data.ErrorCode.PropertyConstraintViolation;
import static de.rwth.idsg.steve.ocpp.ws.data.ErrorCode.ProtocolError;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.12.2026
 */
public class DeserializerTest {

    @Test
    public void testValidation_Ocpp16TypoInEnum() {
        Deserializer des = createDeserializer();

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2, "abc1","StatusNotification",{"connectorId":1,"status":"Faultd","errorCode":"NoError","info":"","timestamp":"2026-01-01T07:00:00.000Z","vendorId":"","vendorErrorCode":""}]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Invalid payload value (cannot understand one field)", error.getErrorDetails());
    }

    @Test
    public void testValidation_Ocpp16MeterValueCascade() {
        Deserializer des = createDeserializer();

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"abc2","MeterValues",{"connectorId":1,"meterValue":[{"timestamp":"2026-02-13T15:17:02.501+01:00"}]}]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Violation of field constraints", error.getErrorDetails());
    }

    @Test
    public void testValidation_Ocpp16IdTagMissing() {
        Deserializer des = createDeserializer();

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"abc3","Authorize",{"idTag":null}]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Violation of field constraints", error.getErrorDetails());
    }

    @Test
    public void testValidation_BrokenPayload() {
        Deserializer des = createDeserializer();

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"abc4","Authorize",{"idTag":"A1B.....]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(FormationViolation, error.getErrorCode());
        Assertions.assertNull(error.getErrorDetails());
    }

    @Test
    public void testValidation_DuplicateMessageId() {
        Deserializer des = createDeserializer(false);

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"dup1","Heartbeat",{}]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(ProtocolError, error.getErrorCode());
        Assertions.assertEquals("dup1", error.getMessageId());
    }

    @Test
    public void testValidation_UnknownSessionContextForMessageIdStore() {
        Deserializer des = createDeserializer(null);

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"unknown1","Heartbeat",{}]
            """);

        des.accept(context);

        OcppJsonMessage outgoingMessage = context.getOutgoingMessage();
        Assertions.assertNotNull(outgoingMessage);
        Assertions.assertInstanceOf(OcppJsonError.class, outgoingMessage);

        OcppJsonError error = (OcppJsonError) outgoingMessage;
        Assertions.assertEquals(InternalError, error.getErrorCode());
        Assertions.assertEquals("unknown1", error.getMessageId());
    }

    @Test
    public void testValidation_FirstSeenMessageIdAccepted() {
        Deserializer des = createDeserializer();

        CommunicationContext context = new CommunicationContext(getMockSession(), "foo");
        context.setIncomingString("""
            [2,"ok1","Heartbeat",{}]
            """);

        des.accept(context);

        Assertions.assertNull(context.getOutgoingMessage());
        Assertions.assertNotNull(context.getIncomingMessage());
        Assertions.assertInstanceOf(OcppJsonCall.class, context.getIncomingMessage());
    }

    private static Deserializer createDeserializer() {
        return createDeserializer(true);
    }

    private static Deserializer createDeserializer(Boolean registerIncomingCallIdResponse) {
        var futureResponseContextStore = new FutureResponseContextStoreImpl();

        SessionContextStore store = Mockito.mock(SessionContextStore.class);
        when(store.registerIncomingCallId(any(), any(), any())).thenReturn(registerIncomingCallIdResponse);

        return new Deserializer(futureResponseContextStore, store, Ocpp16TypeStore.INSTANCE);
    }

    private static JettyWebSocketSession getMockSession() {
        JettyWebSocketSession session = Mockito.mock(JettyWebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getId()).thenReturn(UUID.randomUUID().toString());
        return session;
    }

}

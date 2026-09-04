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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStoreImpl;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import ocpp.cp._2015._10.UpdateFirmwareResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
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

        OcppJsonError error = deserializeError(des, """
            [2, "abc1","StatusNotification",{"connectorId":1,"status":"Faultd","errorCode":"NoError","info":"","timestamp":"2026-01-01T07:00:00.000Z","vendorId":"","vendorErrorCode":""}]
            """);

        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Invalid payload value (cannot understand one field)", error.getErrorDetails());
    }

    @Test
    public void testValidation_Ocpp16MeterValueCascade() {
        Deserializer des = createDeserializer();

        OcppJsonError error = deserializeError(des, """
            [2,"abc2","MeterValues",{"connectorId":1,"meterValue":[{"timestamp":"2026-02-13T15:17:02.501+01:00"}]}]
            """);

        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Violation of field constraints", error.getErrorDetails());
    }

    @Test
    public void testValidation_Ocpp16IdTagMissing() {
        Deserializer des = createDeserializer();

        OcppJsonError error = deserializeError(des, """
            [2,"abc3","Authorize",{"idTag":null}]
            """);

        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());
        Assertions.assertEquals("Violation of field constraints", error.getErrorDetails());
    }

    @Test
    public void testValidation_BrokenPayload() {
        Deserializer des = createDeserializer();

        OcppJsonError error = deserializeError(des, """
            [2,"abc4","Authorize",{"idTag":"A1B.....]
            """);

        Assertions.assertEquals(FormationViolation, error.getErrorCode());
        Assertions.assertNull(error.getErrorDetails());
    }

    @Test
    public void testValidation_DuplicateMessageId() {
        Deserializer des = createDeserializer(false);

        OcppJsonError error = deserializeError(des, """
            [2,"dup1","Heartbeat",{}]
            """);

        Assertions.assertEquals(ProtocolError, error.getErrorCode());
        Assertions.assertEquals("dup1", error.getMessageId());
    }

    @Test
    public void testValidation_UnknownSessionContextForMessageIdStore() {
        Deserializer des = createDeserializer(null);

        OcppJsonError error = deserializeError(des, """
            [2,"unknown1","Heartbeat",{}]
            """);

        Assertions.assertEquals(InternalError, error.getErrorCode());
        Assertions.assertEquals("unknown1", error.getMessageId());
    }

    @Test
    public void testValidation_FirstSeenMessageIdAccepted() {
        Deserializer des = createDeserializer();

        CommunicationContext.DeserializationResult result = Assertions.assertDoesNotThrow(() -> des.accept(inbound("""
            [2,"ok1","Heartbeat",{}]
            """)));

        CommunicationContext.InCall call = Assertions.assertInstanceOf(CommunicationContext.InCall.class, result);
        Assertions.assertInstanceOf(OcppJsonCall.class, call.call());
    }

    @Test
    public void testValidation_EmptyMessageIdRejected() {
        Deserializer des = createDeserializer();

        OcppJsonError error = deserializeError(des, """
            [2,"","Heartbeat",{}]
            """);

        Assertions.assertEquals(FormationViolation, error.getErrorCode());
        Assertions.assertEquals("", error.getMessageId());
    }

    @Test
    public void testValidation_NullMessageIdRejected() {
        Deserializer des = createDeserializer();

        OcppJsonError error = deserializeError(des, """
            [2,null,"Heartbeat",{}]
            """);

        Assertions.assertEquals(FormationViolation, error.getErrorCode());
        Assertions.assertNull(error.getMessageId());
    }

    @Test
    public void testCallResultArrivedBeforeDeadlineIsAcceptedAfterDeadline() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        String messageId = "result-before-deadline";
        Instant deadline = Instant.parse("2026-01-01T00:00:30Z");
        Instant arrivedAt = deadline.minusMillis(1);
        FutureResponseContext responseContext = responseContextExpiringAt(deadline);
        Deserializer deserializer = createDeserializerWith(storeReturning(sessionId, messageId, responseContext));

        CommunicationContext.DeserializationResult result = deserializer.accept(inbound(
            sessionId,
            arrivedAt,
            "[3,\"" + messageId + "\",{}]"
        ));

        CommunicationContext.InResult inResult = Assertions.assertInstanceOf(CommunicationContext.InResult.class, result);
        Assertions.assertSame(responseContext, inResult.frc());
        Assertions.assertInstanceOf(UpdateFirmwareResponse.class, inResult.result().getPayload());
        Mockito.verify(responseContext).hasTimedOut(arrivedAt);
    }

    @Test
    public void testCallErrorArrivedBeforeDeadlineIsAcceptedAfterDeadline() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        String messageId = "error-before-deadline";
        Instant deadline = Instant.parse("2026-01-01T00:00:30Z");
        Instant arrivedAt = deadline.minusMillis(1);
        FutureResponseContext responseContext = responseContextExpiringAt(deadline);
        Deserializer deserializer = createDeserializerWith(storeReturning(sessionId, messageId, responseContext));

        CommunicationContext.DeserializationResult result = deserializer.accept(inbound(
            sessionId,
            arrivedAt,
            "[4,\"" + messageId + "\",\"InternalError\",\"\",{}]"
        ));

        CommunicationContext.InError inError = Assertions.assertInstanceOf(CommunicationContext.InError.class, result);
        Assertions.assertSame(responseContext, inError.frc());
        Assertions.assertEquals(InternalError, inError.error().getErrorCode());
        Mockito.verify(responseContext).hasTimedOut(arrivedAt);
    }

    @Test
    public void testResponseArrivedAfterDeadlineIsRejected() {
        String sessionId = UUID.randomUUID().toString();
        String messageId = "result-after-deadline";
        Instant deadline = Instant.parse("2026-01-01T00:00:30Z");
        Instant arrivedAt = deadline.plusMillis(1);
        FutureResponseContext responseContext = responseContextExpiringAt(deadline);
        Deserializer deserializer = createDeserializerWith(storeReturning(sessionId, messageId, responseContext));

        var exception = Assertions.assertThrows(
            CommunicationContext.JsonResponseInvalidException.class,
            () -> deserializer.accept(inbound(
                sessionId,
                arrivedAt,
                "[3,\"" + messageId + "\",{}]"
            ))
        );

        Assertions.assertEquals("A response message was received to an expired call", exception.getMessage());
        Assertions.assertSame(responseContext, exception.getFrc());
        Mockito.verify(responseContext).hasTimedOut(arrivedAt);
    }

    private static OcppJsonError deserializeError(Deserializer deserializer, String payload) {
        var exception = Assertions.assertThrows(
            CommunicationContext.JsonCallParseException.class,
            () -> deserializer.accept(inbound(payload))
        );
        return exception.getParseError();
    }

    private static CommunicationContext.In inbound(String payload) {
        return inbound(UUID.randomUUID().toString(), Instant.now(), payload);
    }

    private static CommunicationContext.In inbound(String sessionId, Instant arrivedAt, String payload) {
        return new CommunicationContext.In(
            "foo",
            OcppProtocol.V_16_JSON,
            sessionId,
            arrivedAt,
            payload);
    }

    private static Deserializer createDeserializer() {
        return createDeserializer(true);
    }

    private static Deserializer createDeserializer(Boolean registerIncomingCallIdResponse) {
        return createDeserializer(new FutureResponseContextStoreImpl(), registerIncomingCallIdResponse);
    }

    private static Deserializer createDeserializerWith(FutureResponseContextStore futureResponseContextStore) {
        return createDeserializer(futureResponseContextStore, true);
    }

    private static Deserializer createDeserializer(FutureResponseContextStore futureResponseContextStore,
                                                   Boolean registerIncomingCallIdResponse) {

        SessionContextStore store = Mockito.mock(SessionContextStore.class);
        when(store.registerIncomingCallId(any(), any(), any())).thenReturn(registerIncomingCallIdResponse);

        SessionContextStoreHolder holder = Mockito.mock(SessionContextStoreHolder.class);
        when(holder.getOrCreate(OcppProtocol.V_16_JSON.getVersion())).thenReturn(store);

        OcppCallHandler handler = Mockito.mock(OcppCallHandler.class);
        when(handler.getVersion()).thenReturn(OcppProtocol.V_16_JSON.getVersion());
        when(handler.getTypeStore()).thenReturn(Ocpp16TypeStore.INSTANCE);

        return new Deserializer(futureResponseContextStore, holder, List.of(handler));
    }

    private static FutureResponseContextStore storeReturning(String sessionId, String messageId,
                                                              FutureResponseContext responseContext) {
        FutureResponseContextStore store = Mockito.mock(FutureResponseContextStore.class);
        when(store.poll(sessionId, messageId)).thenReturn(responseContext);
        return store;
    }

    private static FutureResponseContext responseContextExpiringAt(Instant deadline) {
        FutureResponseContext responseContext = Mockito.mock(FutureResponseContext.class);
        Mockito.doReturn(UpdateFirmwareResponse.class).when(responseContext).getResponseClass();
        when(responseContext.hasTimedOut(any())).thenAnswer(invocation -> {
            Instant time = invocation.getArgument(0);
            return deadline.isBefore(time);
        });
        return responseContext;
    }

}

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
package de.rwth.idsg.steve.utils;

import com.google.common.net.HttpHeaders;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResponse;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opentest4j.AssertionFailedError;
import org.springframework.boot.web.server.Ssl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.eclipse.jetty.websocket.api.Callback.NOOP;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2018
 */
@Slf4j
@WebSocket
public class OcppJsonChargePoint {

    private final List<String> versions;
    private final String chargeBoxId;
    private final String connectionPath;
    private final CountDownLatch closeHappenedSignal;
    private final Deque<ExchangeContext> exchangeQueue;

    private final Thread testerThread;
    private RuntimeException testerThreadInterruptReason;

    private WebSocketClient client;
    private Session session;

    public OcppJsonChargePoint(OcppVersion version, String chargeBoxId, String pathPrefix) {
        this(List.of(version.getValue()), chargeBoxId, pathPrefix);
    }

    public OcppJsonChargePoint(List<String> ocppVersions, String chargeBoxId, String pathPrefix) {
        this.versions = ocppVersions;
        this.chargeBoxId = chargeBoxId;
        this.connectionPath = pathPrefix + chargeBoxId;
        this.testerThread = Thread.currentThread();
        this.exchangeQueue = new ConcurrentLinkedDeque<>();
        this.closeHappenedSignal = new CountDownLatch(1);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public OcppJsonChargePoint start() {
        return startWithProfile0();
    }

    public OcppJsonChargePoint startWithProfile0() {
        return startInternal(null, null, OcppSecurityProfile.Profile_0);
    }

    public OcppJsonChargePoint startWithProfile1(@NotNull String basicAuthPassword) {
        return startInternal(basicAuthPassword, null, OcppSecurityProfile.Profile_1);
    }

    public OcppJsonChargePoint startWithProfile2(@NotNull String basicAuthPassword, @NotNull Ssl serverSide) {
        return startInternal(basicAuthPassword, serverSide, OcppSecurityProfile.Profile_2);
    }

    public OcppJsonChargePoint startWithProfile3(@NotNull Ssl clientAndServerSide) {
        return startInternal(null, clientAndServerSide, OcppSecurityProfile.Profile_3);
    }

    private OcppJsonChargePoint startInternal(String basicAuthPassword, Ssl ssl, OcppSecurityProfile profile) {
        try {
            ClientUpgradeRequest request = new ClientUpgradeRequest(new URI(connectionPath));
            if (!CollectionUtils.isEmpty(versions)) {
                request.setSubProtocols(versions);
            }

            if (profile.requiresBasicAuth()) {
                Objects.requireNonNull(basicAuthPassword, "basicAuthPassword must be set");
                String encoding = Base64.getEncoder().encodeToString((chargeBoxId + ":" + basicAuthPassword).getBytes(StandardCharsets.UTF_8));
                request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
            }

            client = createWebSocketClient(ssl, profile);
            client.start();

            Future<Session> connect = client.connect(this, request);
            this.session = connect.get(); // block until session is created
            return this;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public List<String> getResponseHeader(String headerName) {
        return session.getUpgradeResponse().getHeaders(headerName);
    }

    public void close() {
        try {
            if (!exchangeQueue.isEmpty()) {
                throw new RuntimeException("Did not exchange all planned communications");
            }
            session.close(StatusCode.NORMAL, "Finished", NOOP);
            boolean closedInTime = closeHappenedSignal.await(30, TimeUnit.SECONDS);
            if (!closedInTime) {
                try {
                    client.stop();
                } catch (Exception stopException) {
                    log.warn("Failed to stop websocket client after close timeout", stopException);
                }
                throw new RuntimeException("Timed out waiting for websocket close event");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Request/response planning
    // -------------------------------------------------------------------------

    public <T extends ResponseType> T send(RequestType payload, Class<T> responseClass) {
        return send(payload, getOperationName(payload), responseClass);
    }

    public <T extends ResponseType> T send(@Nullable RequestType payload, String action, Class<T> responseClass) {
        var incoming = sendInternal(payload, action, responseClass);

        if (incoming instanceof OcppJsonResult result) {
            if (responseClass.isInstance(result.getPayload())) {
                return (T) result.getPayload();
            } else {
                throw new AssertionFailedError("Response payload is not expected class type");
            }
        } else {
            throw new AssertionFailedError("Was expecting OcppJsonResult");
        }
    }

    public OcppJsonError send(RequestType payload) {
        var incoming = sendInternal(payload, getOperationName(payload), null);

        if (incoming instanceof OcppJsonError error) {
            return error;
        } else {
            throw new AssertionFailedError("Was expecting OcppJsonError");
        }
    }

    public void expectRequest(RequestType expectedRequest, ResponseType plannedResponse) {
        OcppJsonResult result = new OcppJsonResult();
        result.setMessageId(null); // must and will be set later from actual incoming request
        result.setPayload(plannedResponse);

        expectRequestInternal(expectedRequest.getClass(), expectedRequest, result);
    }

    public void expectRequest(RequestType expectedRequest, ErrorCode plannedErrorCode) {
        OcppJsonError error = new OcppJsonError();
        error.setMessageId(null); // must and will be set later from actual incoming request
        error.setErrorCode(plannedErrorCode);

        expectRequestInternal(expectedRequest.getClass(), expectedRequest, error);
    }

    /**
     * in some newer test cases, we cannot expect a predefined/fixed request payload, since it is dynamically
     * generated which we cannot anticipate before. but, we can return the request payload that just arrived
     * to the caller, for the caller to do some after-the-fact validation.
     */
    public <T extends RequestType> T expectRequest(Class<T> requestClass, ResponseType plannedResponse) {
        OcppJsonResult result = new OcppJsonResult();
        result.setMessageId(null); // must and will be set later from actual incoming request
        result.setPayload(plannedResponse);

        return expectRequestInternal(requestClass, null, result);
    }

    private <T extends ResponseType> OcppJsonMessage sendInternal(@Nullable RequestType payload,
                                                                  @NotNull String action,
                                                                  @Nullable Class<T> responseClass) {
        String messageId = UUID.randomUUID().toString();

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setPayload(payload);
        call.setAction(action);

        JettyWebSocketSession webSocketSession = new JettyWebSocketSession(Map.of());
        webSocketSession.initializeNativeSession(session);

        ExchangeContext ctx = new ExchangeContext(webSocketSession, chargeBoxId);
        ctx.setOutgoingMessage(call);
        ctx.setResponseClass(responseClass == null ? null : (Class<ResponseType>) responseClass);
        exchangeQueue.add(ctx);

        Serializer.INSTANCE.accept(ctx);

        try {
            session.sendText(ctx.getOutgoingString(), NOOP);
        } catch (Exception e) {
            log.error("Exception", e);
        }

        // wait for the response to arrive and be processed
        try {
            boolean completedInTime = ctx.doneSignal.await(30, TimeUnit.SECONDS);
            if (!completedInTime) {
                throw new AssertionFailedError("Timed out waiting for response. action=" + action + ", messageId=" + messageId);
            }
        } catch (InterruptedException e) {
            if (testerThreadInterruptReason != null) {
                throw testerThreadInterruptReason;
            }
            throw new RuntimeException(e);
        }

        return ctx.getIncomingMessage();
    }

    private <T extends RequestType> T expectRequestInternal(Class<T> requestClass,
                                                            @Nullable RequestType expectedRequest,
                                                            OcppJsonResponse preparedResponse) {
        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(null); // must and will be set later from actual incoming request
        call.setAction(getOperationName(requestClass));
        call.setPayload(expectedRequest);

        JettyWebSocketSession webSocketSession = new JettyWebSocketSession(Map.of());
        webSocketSession.initializeNativeSession(session);

        ExchangeContext ctx = new ExchangeContext(webSocketSession, chargeBoxId);
        ctx.setIncomingMessage(call);
        ctx.setOutgoingMessage(preparedResponse);
        ctx.setRequestClass((Class<RequestType>) requestClass);

        exchangeQueue.add(ctx);

        // wait for the call to arrive and be responded with
        try {
            boolean completedInTime = ctx.doneSignal.await(30, TimeUnit.SECONDS);
            if (!completedInTime) {
                throw new AssertionFailedError("Timed out waiting for expected request. action=" + call.getAction());
            }
            return (T) call.getPayload();
        } catch (InterruptedException e) {
            if (testerThreadInterruptReason != null) {
                throw testerThreadInterruptReason;
            }
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Websocket and message processing stuff
    // -------------------------------------------------------------------------

    @OnWebSocketOpen
    public void onConnect(Session session) {
        // No-op
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        try {
            this.client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.session = null;
        this.closeHappenedSignal.countDown();
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        log.error("Exception", throwable);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

        try (JsonParser parser = mapper.createParser(msg)) {
            parser.nextToken(); // set cursor to '['

            parser.nextToken();
            int messageTypeNr = parser.getIntValue();

            parser.nextToken();
            String messageId = parser.getString();

            MessageType messageType = MessageType.fromTypeNr(messageTypeNr);
            switch (messageType) {
                case CALL_RESULT -> handleResult(messageId, parser);
                case CALL_ERROR -> handleError(messageId, parser);
                case CALL -> handleCall(messageId, parser);
            }
        } catch (Throwable e) {
            testerThreadInterruptReason = new RuntimeException(e);
            testerThread.interrupt();
        }
    }

    private void handleResult(String messageId, JsonParser parser) throws Exception {
        parser.nextToken();
        JsonNode responsePayload = parser.readValueAsTree();

        ExchangeContext exchangeContext = exchangeQueue.poll();
        if (exchangeContext == null) {
            throw new RuntimeException("Unexpected message");
        }

        if (!Objects.equals(exchangeContext.getOutgoingMessage().getMessageId(), messageId)) {
            throw new RuntimeException("Unexpected order of exchanges");
        }

        Class<ResponseType> responseClass = exchangeContext.getResponseClass();
        if (responseClass == null) {
            throw new RuntimeException("Was not expecting a result: responseClass was not set");
        }
        ResponseType res = JsonObjectMapper.INSTANCE.getMapper().treeToValue(responsePayload, responseClass);

        OcppJsonResult result = new OcppJsonResult();
        result.setMessageId(messageId);
        result.setPayload(res);

        exchangeContext.setIncomingMessage(result);
        exchangeContext.doneSignal.countDown();
    }

    private void handleError(String messageId, JsonParser parser) throws Exception {
        parser.nextToken();
        ErrorCode code = ErrorCode.fromValue(parser.getString());

        parser.nextToken();
        String desc = parser.getString();
        if ("".equals(desc)) {
            desc = null;
        }

        String details = null;
        parser.nextToken();
        TreeNode detailsNode = parser.readValueAsTree();
        if (detailsNode != null && detailsNode.size() != 0) {
            details = JsonObjectMapper.INSTANCE.getMapper().writeValueAsString(detailsNode);
        }

        OcppJsonError error = new OcppJsonError();
        error.setMessageId(messageId);
        error.setErrorCode(code);
        error.setErrorDescription(desc);
        error.setErrorDetails(details);

        ExchangeContext exchangeContext = exchangeQueue.poll();
        if (exchangeContext == null) {
            throw new RuntimeException("Was not expecting any message");
        }

        if (!Objects.equals(exchangeContext.getOutgoingMessage().getMessageId(), messageId)) {
            throw new RuntimeException("Unexpected order of exchanges");
        }

        exchangeContext.setIncomingMessage(error);
        exchangeContext.doneSignal.countDown();
    }

    private void handleCall(String messageId, JsonParser parser) {
        // parse action
        //
        parser.nextToken();
        String action = parser.getString();

        // parse request payload
        //
        parser.nextToken();
        JsonNode requestPayload = parser.readValueAsTree();
        // https://github.com/steve-community/steve/issues/1109
        if (requestPayload instanceof NullNode) {
            requestPayload = new ObjectNode(JsonNodeFactory.instance);
        }
        String req = requestPayload.toString();

        ExchangeContext exchangeContext = exchangeQueue.poll();
        if (exchangeContext == null) {
            throw new RuntimeException("Was not expecting any message, but received: " + req);
        }

        var incoming = exchangeContext.getIncomingMessage();
        if (incoming == null) {
            throw new RuntimeException("Was not expecting this incoming message: " + req);
        }

        if (!(incoming instanceof OcppJsonCall preparedCall)) {
            throw new RuntimeException("Incoming is not a Call message");
        }

        if (!Objects.equals(preparedCall.getAction(), action)) {
            throw new RuntimeException("Unexpected action: " + action + ". Was expecting: " + preparedCall.getAction());
        }

        Class<RequestType> requestClass = exchangeContext.getRequestClass();
        var actualReqData = JsonObjectMapper.INSTANCE.getMapper().readValue(req, requestClass);

        // in some newer test cases, we cannot expect a predefined/fixed request payload, since it is dynamically
        // generated which we cannot anticipate before. so we can do strict full-payload equality, only if it is set.
        // in other cases, set the incoming request for the caller to validate after-the-fact.
        //
        RequestType payload = preparedCall.getPayload();
        if (payload == null) {
            preparedCall.setPayload(actualReqData);
        } else {
            if (!Objects.equals(actualReqData.toString(), payload.toString())) {
                throw new RuntimeException("Unexpected message: " + actualReqData + ". Was expecting: " + payload);
            }
        }

        preparedCall.setMessageId(messageId);
        var outgoing = exchangeContext.getOutgoingMessage();
        outgoing.setMessageId(messageId);

        Serializer.INSTANCE.accept(exchangeContext);

        try {
            session.sendText(exchangeContext.getOutgoingString(), NOOP);
        } catch (Exception e) {
            log.error("Exception", e);
        }

        exchangeContext.doneSignal.countDown();
    }

    // -------------------------------------------------------------------------
    // Private static helpers
    // -------------------------------------------------------------------------

    private static String getOperationName(Class<?> requestClass) {
        String s = requestClass.getSimpleName();
        if (s.endsWith("Request")) {
            s = s.substring(0, s.length() - 7);
        }
        return s;
    }

    private static String getOperationName(RequestType requestType) {
        return getOperationName(requestType.getClass());
    }

    /**
     * Keep SSL scoped to this test client instance. Do not mutate global JVM SSL properties.
     */
    private static WebSocketClient createWebSocketClient(Ssl serverSslConfig, OcppSecurityProfile profile) {
        if (serverSslConfig == null || !serverSslConfig.isEnabled() || !profile.requiresServerTLS()) {
            return new WebSocketClient();
        }

        var sslContextFactory = new SslContextFactory.Client();

        // server-side certs for profiles 2 and 3
        if (profile.requiresServerTLS()) {
            // server's keystore is client's truststore
            sslContextFactory.setTrustStorePath(serverSslConfig.getKeyStore());
            sslContextFactory.setTrustStorePassword(serverSslConfig.getKeyStorePassword());
            sslContextFactory.setTrustStoreType(serverSslConfig.getKeyStoreType());
        }

        // Optionally present client certificate for mTLS (security profile 3).
        if (profile.requiresClientTLS()) {
            // server's truststore is client's keystore
            sslContextFactory.setKeyStorePath(serverSslConfig.getTrustStore());
            sslContextFactory.setKeyStorePassword(serverSslConfig.getTrustStorePassword());
            sslContextFactory.setKeyStoreType(serverSslConfig.getTrustStoreType());
        }

        var httpClient = new HttpClient();
        httpClient.setSslContextFactory(sslContextFactory);
        return new WebSocketClient(httpClient);
    }

    @Setter
    @Getter
    private static class ExchangeContext extends CommunicationContext {

        private Class<RequestType> requestClass;
        private Class<ResponseType> responseClass;
        private CountDownLatch doneSignal = new CountDownLatch(1);

        public ExchangeContext(@NotNull WebSocketSession session, @NotNull String chargeBoxId) {
            super(session, chargeBoxId);
        }
    }
}

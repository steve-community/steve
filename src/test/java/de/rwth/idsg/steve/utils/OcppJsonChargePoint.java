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
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    private final String basicAuthPassword;
    private final WebSocketClient client;
    private final CountDownLatch closeHappenedSignal;
    private final Deque<ExchangeContext> exchangeQueue;

    private final Thread testerThread;
    private RuntimeException testerThreadInterruptReason;

    private Session session;

    public OcppJsonChargePoint(OcppVersion version, String chargeBoxId, String pathPrefix) {
        this(List.of(version.getValue()), chargeBoxId, pathPrefix, null);
    }

    public OcppJsonChargePoint(OcppVersion version, String chargeBoxId, String pathPrefix, String basicAuthPassword) {
        this(List.of(version.getValue()), chargeBoxId, pathPrefix, basicAuthPassword);
    }

    public OcppJsonChargePoint(List<String> ocppVersions, String chargeBoxId, String pathPrefix, String basicAuthPassword) {
        this.versions = ocppVersions;
        this.chargeBoxId = chargeBoxId;
        this.connectionPath = pathPrefix + chargeBoxId;
        this.basicAuthPassword = basicAuthPassword;
        this.client = new WebSocketClient();
        this.testerThread = Thread.currentThread();
        this.exchangeQueue = new ArrayDeque<>();
        this.closeHappenedSignal = new CountDownLatch(1);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public OcppJsonChargePoint start() {
        try {
            ClientUpgradeRequest request = new ClientUpgradeRequest(new URI(connectionPath));
            if (!CollectionUtils.isEmpty(versions)) {
                request.setSubProtocols(versions);
            }
            if (basicAuthPassword != null) {
                String encoding = Base64.getEncoder().encodeToString((chargeBoxId + ":" + basicAuthPassword).getBytes());
                request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
            }
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
            closeHappenedSignal.await(30, TimeUnit.SECONDS);
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

        expectRequestInternal(expectedRequest, result);
    }

    public void expectRequest(RequestType expectedRequest, ErrorCode plannedErrorCode) {
        OcppJsonError error = new OcppJsonError();
        error.setMessageId(null); // must and will be set later from actual incoming request
        error.setErrorCode(plannedErrorCode);

        expectRequestInternal(expectedRequest, error);
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
            ctx.doneSignal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            if (testerThreadInterruptReason != null) {
                throw testerThreadInterruptReason;
            }
            throw new RuntimeException(e);
        }

        return ctx.getIncomingMessage();
    }

    private void expectRequestInternal(RequestType expectedRequest, OcppJsonResponse preparedResponse) {
        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(null); // must and will be set later from actual incoming request
        call.setAction(getOperationName(expectedRequest));
        call.setPayload(expectedRequest);

        JettyWebSocketSession webSocketSession = new JettyWebSocketSession(Map.of());
        webSocketSession.initializeNativeSession(session);

        ExchangeContext ctx = new ExchangeContext(webSocketSession, chargeBoxId);
        ctx.setIncomingMessage(call);
        ctx.setOutgoingMessage(preparedResponse);

        exchangeQueue.add(ctx);

        // wait for the call to arrive and be responded with
        try {
            ctx.doneSignal.await(30, TimeUnit.SECONDS);
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

        var actualReqData = JsonObjectMapper.INSTANCE.getMapper().readValue(req, preparedCall.getPayload().getClass());
        if (!Objects.equals(actualReqData.toString(), preparedCall.getPayload().toString())) {
            throw new RuntimeException("Unexpected message: " + actualReqData + ". Was expecting: " + preparedCall.getPayload());
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

    private static String getOperationName(RequestType requestType) {
        String s = requestType.getClass().getSimpleName();
        if (s.endsWith("Request")) {
            s = s.substring(0, s.length() - 7);
        }
        return s;
    }

    @Setter
    @Getter
    private static class ExchangeContext extends CommunicationContext {

        private Class<ResponseType> responseClass;
        private CountDownLatch doneSignal = new CountDownLatch(1);

        public ExchangeContext(@NotNull WebSocketSession session, @NotNull String chargeBoxId) {
            super(session, chargeBoxId);
        }
    }
}

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
package de.rwth.idsg.steve.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppVersion;
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
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.eclipse.jetty.websocket.api.Callback.NOOP;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2018
 */
@Slf4j
@WebSocket
public class OcppJsonChargePoint {

    private final ObjectMapper ocppMapper;
    private final String version;
    private final String chargeBoxId;
    private final String connectionPath;
    private final Map<String, ResponseContext> responseContextMap;
    private final Map<String, RequestContext> requestContextMap;
    private final MessageDeserializer deserializer;
    private final WebSocketClient client;
    private final CountDownLatch closeHappenedSignal;
    private final Serializer serializer;

    private final Thread testerThread;
    private RuntimeException testerThreadInterruptReason;

    private CountDownLatch receivedMessagesSignal;
    private Session session;

    public OcppJsonChargePoint(ObjectMapper ocppMapper, OcppVersion version, String chargeBoxId, String pathPrefix) {
        this(ocppMapper, version.getValue(), chargeBoxId, pathPrefix);
    }

    public OcppJsonChargePoint(ObjectMapper ocppMapper, String ocppVersion, String chargeBoxId, String pathPrefix) {
        this.ocppMapper = ocppMapper;
        this.version = ocppVersion;
        this.chargeBoxId = chargeBoxId;
        this.connectionPath = pathPrefix + chargeBoxId;
        this.responseContextMap = new LinkedHashMap<>(); // because we want to keep the insertion order of test cases
        this.requestContextMap = new HashMap<>();
        this.deserializer = new MessageDeserializer();
        this.client = new WebSocketClient();
        this.closeHappenedSignal = new CountDownLatch(1);
        this.testerThread = Thread.currentThread();
        this.serializer = new Serializer(ocppMapper);
    }

    @OnWebSocketOpen
    public void onConnect(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        this.session = null;
        this.closeHappenedSignal.countDown();
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        log.error("Exception", throwable);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            OcppJsonMessage ocppMsg = deserializer.extract(msg);

            if (ocppMsg instanceof OcppJsonResult) {
                ResponseContext ctx = responseContextMap.remove(ocppMsg.getMessageId());
                ctx.responseHandler.accept(((OcppJsonResult) ocppMsg).getPayload());
            } else if (ocppMsg instanceof OcppJsonError) {
                ResponseContext ctx = responseContextMap.remove(ocppMsg.getMessageId());
                ctx.errorHandler.accept((OcppJsonError) ocppMsg);
            } else if (ocppMsg instanceof OcppJsonCallForTesting) {
                handleCall((OcppJsonCallForTesting) ocppMsg);
            }
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (receivedMessagesSignal != null) {
                receivedMessagesSignal.countDown();
            }
        }
    }

    public void start() {
        try {
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            if (version != null) {
                request.setSubProtocols(version);
            }

            client.start();

            Future<Session> connect = client.connect(this, new URI(connectionPath), request);
            connect.get(); // block until session is created
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public <T extends ResponseType> void prepare(RequestType request, Class<T> responseClass,
                                                 Consumer<T> responseHandler, Consumer<OcppJsonError> errorHandler) {
        prepare(request, getOperationName(request), responseClass, responseHandler, errorHandler);
    }

    public <T extends ResponseType> void prepare(RequestType payload, String action, Class<T> responseClass,
                                                 Consumer<T> responseHandler, Consumer<OcppJsonError> errorHandler) {
        String messageId = UUID.randomUUID().toString();

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setPayload(payload);
        call.setAction(action);

        // session is null, because we do not need org.springframework.web.socket.WebSocketSession
        CommunicationContext ctx = new CommunicationContext(null, chargeBoxId);
        ctx.setOutgoingMessage(call);

        serializer.accept(ctx);

        ResponseContext resCtx = new ResponseContext(ctx.getOutgoingString(), responseClass, responseHandler, errorHandler);
        responseContextMap.put(messageId, resCtx);
    }

    public void expectRequest(RequestType expectedRequest, ResponseType plannedResponse) {
        String requestPayload;
        JsonNode responsePayload;
        try {
            requestPayload = ocppMapper.writeValueAsString(expectedRequest);
            responsePayload = ocppMapper.valueToTree(plannedResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String action = getOperationName(expectedRequest);
        requestContextMap.put(action, new RequestContext(requestPayload, responsePayload));
    }

    public void process() {
        int requestCount = requestContextMap.values().size();
        int responseCount = responseContextMap.values().size();
        receivedMessagesSignal = new CountDownLatch(requestCount + responseCount);

        // copy the values in a new list to be iterated over, because otherwise we get a ConcurrentModificationException,
        // since the onMessage(..) uses the same responseContextMap to remove an item while looping over its items here.
        ArrayList<ResponseContext> values = new ArrayList<>(responseContextMap.values());

        // send all messages
        for (ResponseContext ctx : values) {
            try {
                session.sendText(ctx.outgoingMessage, NOOP);
            } catch (Exception e) {
                log.error("Exception", e);
            }
        }

        // wait for all responses to arrive and be processed
        try {
            receivedMessagesSignal.await();
        } catch (InterruptedException e) {
            if (testerThreadInterruptReason != null) {
                throw testerThreadInterruptReason;
            }
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            // "enqueue" a graceful close
            session.close(StatusCode.NORMAL, "Finished", NOOP);

            // wait for close to happen
            closeHappenedSignal.await();

            // well, stop the client
            client.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processAndClose() {
        process();
        close();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String getOperationName(RequestType requestType) {
        String s = requestType.getClass().getSimpleName();
        if (s.endsWith("Request")) {
            s = s.substring(0, s.length() - 7);
        }
        return s;
    }

    private void handleCall(OcppJsonCallForTesting call) {
        try {
            ArrayNode node = ocppMapper
                .createArrayNode()
                .add(MessageType.CALL_RESULT.getTypeNr())
                .add(call.getMessageId())
                .add(call.getContext().getResponsePayload());

            String str = ocppMapper.writeValueAsString(node);
            session.sendText(str, NOOP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Value
    private static class RequestContext {
        String requestPayload;
        JsonNode responsePayload;
    }

    private static class ResponseContext {
        private final String outgoingMessage;
        private final Class<ResponseType> responseClass;
        private final Consumer<ResponseType> responseHandler;
        private final Consumer<OcppJsonError> errorHandler;

        @SuppressWarnings("unchecked")
        private <T extends ResponseType> ResponseContext(String outgoingMessage,
                                                         Class<T> responseClass,
                                                         Consumer<T> responseHandler,
                                                         Consumer<OcppJsonError> errorHandler) {
            this.outgoingMessage = outgoingMessage;
            this.responseClass = (Class<ResponseType>) responseClass;
            this.responseHandler = (Consumer<ResponseType>) responseHandler;
            this.errorHandler = errorHandler;
        }
    }

    private class MessageDeserializer {

        private OcppJsonMessage extract(String msg) throws Exception {

            try (JsonParser parser = ocppMapper.getFactory().createParser(msg)) {
                parser.nextToken(); // set cursor to '['

                parser.nextToken();
                int messageTypeNr = parser.getIntValue();

                parser.nextToken();
                String messageId = parser.getText();

                MessageType messageType = MessageType.fromTypeNr(messageTypeNr);
                switch (messageType) {
                    case CALL_RESULT:
                        return handleResult(messageId, parser);
                    case CALL_ERROR:
                        return handleError(messageId, parser);
                    case CALL:
                        return handleCall(messageId, parser);
                    default:
                        throw new SteveException("Unknown enum type");
                }
            }
        }

        private OcppJsonResponse handleResult(String messageId, JsonParser parser) throws Exception {
            parser.nextToken();
            JsonNode responsePayload = parser.readValueAsTree();
            Class<ResponseType> clazz = responseContextMap.get(messageId).responseClass;
            ResponseType res = ocppMapper.treeToValue(responsePayload, clazz);

            OcppJsonResult result = new OcppJsonResult();
            result.setMessageId(messageId);
            result.setPayload(res);
            return result;
        }

        private OcppJsonResponse handleError(String messageId, JsonParser parser) throws Exception {
            parser.nextToken();
            ErrorCode code = ErrorCode.fromValue(parser.getText());

            parser.nextToken();
            String desc = parser.getText();
            if ("".equals(desc)) {
                desc = null;
            }

            String details = null;
            parser.nextToken();
            TreeNode detailsNode = parser.readValueAsTree();
            if (detailsNode != null && detailsNode.size() != 0) {
                details = ocppMapper.writeValueAsString(detailsNode);
            }

            OcppJsonError error = new OcppJsonError();
            error.setMessageId(messageId);
            error.setErrorCode(code);
            error.setErrorDescription(desc);
            error.setErrorDetails(details);
            return error;
        }

        private OcppJsonCall handleCall(String messageId, JsonParser parser) {
            // parse action
            String action;
            try {
                parser.nextToken();
                action = parser.getText();
            } catch (IOException e) {
                throw new RuntimeException();
            }

            // parse request payload
            String req;
            try {
                parser.nextToken();
                JsonNode requestPayload = parser.readValueAsTree();

                // https://github.com/steve-community/steve/issues/1109
                if (requestPayload instanceof NullNode) {
                    requestPayload = new ObjectNode(JsonNodeFactory.instance);
                }

                req = requestPayload.toString();
            } catch (IOException e) {
                log.error("Exception occurred", e);
                throw new RuntimeException();
            }

            RequestContext context = requestContextMap.get(action);
            if (context == null) {
                testerThreadInterruptReason = new RuntimeException("Unexpected message arrived: " + req);
                testerThread.interrupt();
            } else if (Objects.equals(context.requestPayload, req)) {
                requestContextMap.remove(action);
            }

            OcppJsonCallForTesting call = new OcppJsonCallForTesting();
            call.setAction(action);
            call.setMessageId(messageId);
            call.setContext(context);
            return call;
        }
    }

    @Setter
    @Getter
    private static class OcppJsonCallForTesting extends OcppJsonCall {
        private RequestContext context;
    }
}

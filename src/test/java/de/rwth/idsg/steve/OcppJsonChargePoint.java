package de.rwth.idsg.steve;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResponse;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2018
 */
@WebSocket
public class OcppJsonChargePoint {

    private final OcppVersion version;
    private final String chargeBoxId;
    private final String connectionPath;
    private final Map<String, ResponseContext> responseContextMap;
    private final ResponseDeserializer deserializer;
    private final WebSocketClient client;
    private final CountDownLatch closeHappenedSignal;

    private CountDownLatch receivedResponsesSignal;
    private Session session;

    OcppJsonChargePoint(OcppVersion version, String chargeBoxId, String pathPrefix) {
        this.version = version;
        this.chargeBoxId = chargeBoxId;
        this.connectionPath = pathPrefix + chargeBoxId;
        this.responseContextMap = new LinkedHashMap<>(); // because we want to keep the insertion order of test cases
        this.deserializer = new ResponseDeserializer();
        this.client = new WebSocketClient();
        this.closeHappenedSignal = new CountDownLatch(1);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        this.session = null;
        this.closeHappenedSignal.countDown();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            OcppJsonResponse response = deserializer.extractResponse(msg);
            ResponseContext ctx = responseContextMap.remove(response.getMessageId());

            if (response instanceof OcppJsonResult) {
                ctx.responseHandler.accept(((OcppJsonResult) response).getPayload());
            } else if (response instanceof OcppJsonError) {
                ctx.errorHandler.accept((OcppJsonError) response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (receivedResponsesSignal != null) {
                receivedResponsesSignal.countDown();
            }
        }
    }

    public void start() {
        try {
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setSubProtocols(version.getValue());

            client.start();

            Future<Session> connect = client.connect(this, new URI(connectionPath), request);
            connect.get(); // block until session is created
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public <T extends ResponseType> void prepare(RequestType request, Class<T> responseClass,
                                                 Consumer<T> responseHandler, Consumer<OcppJsonError> errorHandler) {
        String messageId = UUID.randomUUID().toString();

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setPayload(request);
        call.setAction(getOperationName(request));

        // session is null, because we do not need org.springframework.web.socket.WebSocketSession
        CommunicationContext ctx = new CommunicationContext(null, chargeBoxId);
        ctx.setOutgoingMessage(call);

        Serializer.INSTANCE.accept(ctx);

        ResponseContext resCtx = new ResponseContext(ctx.getOutgoingString(), responseClass, responseHandler, errorHandler);
        responseContextMap.put(messageId, resCtx);
    }

    public void processAndClose() {
        Collection<ResponseContext> values = responseContextMap.values();
        receivedResponsesSignal = new CountDownLatch(values.size());

        // send all messages
        for (ResponseContext ctx : values) {
            try {
                session.getRemote().sendString(ctx.outgoingMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // wait for all responses to arrive and be processed
            receivedResponsesSignal.await(15, TimeUnit.SECONDS);

            // "enqueue" a graceful close
            session.close(StatusCode.NORMAL, "Finished");

            // wait for close to happen
            closeHappenedSignal.await(15, TimeUnit.SECONDS);

            // well, stop the client
            client.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private class ResponseDeserializer {

        private OcppJsonResponse extractResponse(String msg) throws Exception {
            ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

            try (JsonParser parser = mapper.getFactory().createParser(msg)) {
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
                    default:
                        throw new SteveException("Unknown enum type");
                }
            }
        }

        private OcppJsonResponse handleResult(String messageId, JsonParser parser) throws Exception {
            parser.nextToken();
            JsonNode responsePayload = parser.readValueAsTree();
            Class<ResponseType> clazz = responseContextMap.get(messageId).responseClass;
            ResponseType res = JsonObjectMapper.INSTANCE.getMapper().treeToValue(responsePayload, clazz);

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
                details = JsonObjectMapper.INSTANCE.getMapper().writeValueAsString(detailsNode);
            }

            OcppJsonError error = new OcppJsonError();
            error.setMessageId(messageId);
            error.setErrorCode(code);
            error.setErrorDescription(desc);
            error.setErrorDetails(details);
            return error;
        }
    }

}

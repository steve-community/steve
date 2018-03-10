package de.rwth.idsg.steve.ocpp.ws.pipeline;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Incoming String --> OcppJsonMessage
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class Deserializer implements Consumer<CommunicationContext> {

    private final ObjectMapper mapper;
    private final FutureResponseContextStore futureResponseContextStore;
    private final TypeStore typeStore;

    /**
     * Parsing with streaming API is cumbersome, but only it allows to parse the String step for step
     * and build, if any, a corresponding error message.
     */
    @Override
    public void accept(CommunicationContext context) {
        try (JsonParser parser = mapper.getFactory().createParser(context.getIncomingString())) {
            parser.nextToken(); // set cursor to '['

            parser.nextToken();
            int messageTypeNr = parser.getIntValue();

            parser.nextToken();
            String messageId = parser.getText();

            MessageType messageType = MessageType.fromTypeNr(messageTypeNr);
            switch (messageType) {
                case CALL:
                    handleCall(context, messageId, parser);
                    break;

                case CALL_RESULT:
                    handleResult(context, messageId, parser);
                    break;

                case CALL_ERROR:
                    handleError(context, messageId, parser);
                    break;

                default:
                    throw new SteveException("Unknown enum type");
            }
        } catch (IOException e) {
            throw new SteveException("Deserialization of incoming string failed: %s", context.getIncomingString(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Private Helpers
    // -------------------------------------------------------------------------

    /**
     * Catch exceptions and wrap them in outgoing ERRORs for incoming CALLs.
     */
    private void handleCall(CommunicationContext context, String messageId, JsonParser parser) {
        // parse action
        String action;
        try {
            parser.nextToken();
            action = parser.getText();
        } catch (IOException e) {
            log.error("Exception occurred", e);
            context.setOutgoingMessage(ErrorFactory.genericDeserializeError(messageId, e.getMessage()));
            return;
        }

        // find action class
        Class<? extends RequestType> clazz = typeStore.findRequestClass(action);
        if (clazz == null) {
            context.setOutgoingMessage(ErrorFactory.actionNotFound(messageId, action));
            return;
        }

        // parse request payload
        RequestType req;
        try {
            parser.nextToken();
            JsonNode requestPayload = parser.readValueAsTree();
            req = mapper.treeToValue(requestPayload, clazz);
        } catch (IOException e) {
            log.error("Exception occurred", e);
            context.setOutgoingMessage(ErrorFactory.payloadDeserializeError(messageId, e.getMessage()));
            return;
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setAction(action);
        call.setPayload(req);

        context.setIncomingMessage(call);
    }

    /**
     * Do NOT catch and handle exceptions for incoming RESPONSEs. Let the processing fail.
     * There is no mechanism in OCPP to report back such erroneous messages.
     */
    private void handleResult(CommunicationContext context, String messageId, JsonParser parser) {
        FutureResponseContext responseContext = futureResponseContextStore.get(context.getSession(), messageId);
        if (responseContext == null) {
            throw new SteveException(
                    "A result message was received as response to a not-sent call. The message was: %s",
                    context.getIncomingString()
            );
        }

        ResponseType res;
        try {
            parser.nextToken();
            JsonNode responsePayload = parser.readValueAsTree();
            res = mapper.treeToValue(responsePayload, responseContext.getResponseClass());
        } catch (IOException e) {
            throw new SteveException("Deserialization of incoming response payload failed", e);
        }

        OcppJsonResult result = new OcppJsonResult();
        result.setMessageId(messageId);
        result.setPayload(res);

        context.setIncomingMessage(result);
        context.createResultHandler(responseContext.getTask());
    }

    /**
     * Do NOT catch and handle exceptions for incoming RESPONSEs. Let the processing fail.
     * There is no mechanism in OCPP to report back such erroneous messages.
     */
    private void handleError(CommunicationContext context, String messageId, JsonParser parser) {
        FutureResponseContext responseContext = futureResponseContextStore.get(context.getSession(), messageId);
        if (responseContext == null) {
            throw new SteveException(
                    "An error message was received as response to a not-sent call. The message was: %s",
                    context.getIncomingString()
            );
        }

        ErrorCode code;
        String desc;
        String details;
        try {
            parser.nextToken();
            code = ErrorCode.fromValue(parser.getText());

            parser.nextToken();
            desc = parser.getText();

            // From spec:
            // ErrorDescription - Should be filled in if possible, otherwise a clear empty string "".
            if ("".equals(desc)) {
                desc = null;
            }

            // From soec:
            // ErrorDetails - This JSON object describes error details in an undefined way.
            // If there are no error details you should fill in an empty object {}, missing or null is not allowed
            details = (parser.nextToken() == JsonToken.START_OBJECT) ? null : parser.getText();

        } catch (IOException e) {
            throw new SteveException("Deserialization of incoming error message failed", e);
        }

        OcppJsonError error = new OcppJsonError();
        error.setMessageId(messageId);
        error.setErrorCode(code);
        error.setErrorDescription(desc);
        error.setErrorDetails(details);

        context.setIncomingMessage(error);
        context.createErrorHandler(responseContext.getTask());
    }
}

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

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Incoming String --> OcppJsonMessage
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Slf4j
@Component
public class Deserializer {

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();
    private final Map<OcppVersion, TypeStore> typeStoreHolder = new EnumMap<>(OcppVersion.class);

    private final FutureResponseContextStore futureResponseContextStore;
    private final SessionContextStoreHolder sessionContextStoreHolder;

    public Deserializer(FutureResponseContextStore futureResponseContextStore,
                        SessionContextStoreHolder sessionContextStoreHolder,
                        List<OcppCallHandler> handlers) {
        this.futureResponseContextStore = futureResponseContextStore;
        this.sessionContextStoreHolder = sessionContextStoreHolder;
        for (OcppCallHandler handler : handlers) {
            typeStoreHolder.put(handler.getVersion(), handler.getTypeStore());
        }
    }

    /**
     * Parsing with streaming API is cumbersome, but only it allows to parse the String step for step
     * and build, if any, a corresponding error message.
     */
    public CommunicationContext.DeserializationResult accept(CommunicationContext.In inMsg) throws CommunicationContext.JsonCallParseException, CommunicationContext.JsonResponseInvalidException {
        try (JsonParser parser = mapper.createParser(inMsg.payload())) {
            parser.nextToken(); // set cursor to '['

            parser.nextToken();
            int messageTypeNr = parser.getIntValue();

            parser.nextToken();
            String messageId = parser.getString();

            return switch (MessageType.fromTypeNr(messageTypeNr)) {
                case CALL -> handleCall(inMsg, messageId, parser);
                case CALL_RESULT -> handleResult(inMsg, messageId, parser);
                case CALL_ERROR -> handleError(inMsg, messageId, parser);
            };
        }
    }

    // -------------------------------------------------------------------------
    // Private Helpers
    // -------------------------------------------------------------------------

    /**
     * Catch exceptions and wrap them in outgoing ERRORs for incoming CALLs.
     */
    private CommunicationContext.InCall handleCall(CommunicationContext.In inMsg, String messageId,
                                                   JsonParser parser) throws CommunicationContext.JsonCallParseException {
        OcppVersion version = inMsg.route().protocol().getVersion();

        // Enforce OCPP CALL messageId as a non-empty JSON string.
        // messageId must be a usable request identifier, so null or empty should be treated as invalid.
        // Token-type check avoids accepting VALUE_NULL cases that can be exposed as text like "null" by streaming accessors.
        JsonToken messageIdToken = parser.currentToken();
        if (messageIdToken != JsonToken.VALUE_STRING || StringUtils.isEmpty(messageId)) {
            var error = ErrorFactory.invalidMessageId(messageIdToken == JsonToken.VALUE_STRING ? messageId : null);
            throw new CommunicationContext.JsonCallParseException(error);
        }

        var sessionContextStore = sessionContextStoreHolder.getOrCreate(version);
        Boolean success = sessionContextStore.registerIncomingCallId(inMsg.route().chargeBoxId(), inMsg.route().webSocketSessionId(), messageId);
        if (success == null) {
            log.warn("No session context found while registering incoming CALL messageId '{}' for sessionId '{}'", messageId, inMsg.route().webSocketSessionId());
            var error = ErrorFactory.payloadProcessingError(messageId, null);
            throw new CommunicationContext.JsonCallParseException(error);
        } else if (!success) {
            var error = ErrorFactory.duplicateCallMessageId(messageId);
            throw new CommunicationContext.JsonCallParseException(error);
        }

        // parse action
        String action;
        try {
            parser.nextToken();
            action = parser.getString();
        } catch (JacksonException e) {
            log.error("Exception occurred", e);
            var error = ErrorFactory.genericDeserializeError(messageId, e.getMessage());
            throw new CommunicationContext.JsonCallParseException(error);
        }

        var typeStore = typeStoreHolder.get(version);
        if (typeStore == null) {
            // should not happen, means impl or config error
            throw new SteveException("Unknown protocol version: " + version);
        }

        // find action class
        Class<? extends RequestType> clazz = typeStore.findRequestClass(action);
        if (clazz == null) {
            var error = ErrorFactory.actionNotFound(messageId, action);
            throw new CommunicationContext.JsonCallParseException(error);
        }

        // parse request payload
        RequestType req;
        try {
            parser.nextToken();
            JsonNode requestPayload = parser.readValueAsTree();

            // https://github.com/steve-community/steve/issues/1109
            if (requestPayload instanceof NullNode) {
                requestPayload = new ObjectNode(JsonNodeFactory.instance);
            }

            req = mapper.treeToValue(requestPayload, clazz);
        } catch (ConstraintViolationException | DatabindException e) {
            log.error("Exception occurred", e);
            var error = ErrorFactory.propertyConstraintViolation(messageId, getDetails(e));
            throw new CommunicationContext.JsonCallParseException(error);
        } catch (JacksonException e) {
            log.error("Exception occurred", e);
            var error = ErrorFactory.payloadDeserializeError(messageId, null);
            throw new CommunicationContext.JsonCallParseException(error);
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(messageId);
        call.setAction(action);
        call.setPayload(req);

        return new CommunicationContext.InCall(inMsg, call);
    }

    /**
     * Do NOT catch and handle exceptions for incoming RESPONSEs. Let the processing fail.
     * There is no mechanism in OCPP to report back such erroneous messages.
     */
    private CommunicationContext.InResult handleResult(CommunicationContext.In inMsg, String messageId,
                                                       JsonParser parser) throws CommunicationContext.JsonResponseInvalidException {
        FutureResponseContext responseContext = futureResponseContextStore.poll(inMsg.route().webSocketSessionId(), messageId);
        validate(responseContext);

        ResponseType res;
        try {
            parser.nextToken();
            JsonNode responsePayload = parser.readValueAsTree();
            res = mapper.treeToValue(responsePayload, responseContext.getResponseClass());
        } catch (JacksonException e) {
            throw new CommunicationContext.JsonResponseInvalidException("Deserialization of incoming response payload failed", e, responseContext);
        }

        OcppJsonResult result = new OcppJsonResult();
        result.setMessageId(messageId);
        result.setPayload(res);

        return new CommunicationContext.InResult(inMsg, result, responseContext);
    }

    /**
     * Do NOT catch and handle exceptions for incoming RESPONSEs. Let the processing fail.
     * There is no mechanism in OCPP to report back such erroneous messages.
     */
    private CommunicationContext.InError handleError(CommunicationContext.In inMsg, String messageId,
                                                     JsonParser parser) throws CommunicationContext.JsonResponseInvalidException {
        FutureResponseContext responseContext = futureResponseContextStore.poll(inMsg.route().webSocketSessionId(), messageId);
        validate(responseContext);

        ErrorCode code;
        String desc;
        String details = null;
        try {
            parser.nextToken();
            code = ErrorCode.fromValue(parser.getString());

            parser.nextToken();
            desc = parser.getString();

            // From spec:
            // ErrorDescription - Should be filled in if possible, otherwise a clear empty string "".
            if ("".equals(desc)) {
                desc = null;
            }

            // From spec:
            // ErrorDetails - This JSON object describes error details in an undefined way.
            // If there are no error details you should fill in an empty object {}, missing or null is not allowed
            parser.nextToken();
            TreeNode detailsNode = parser.readValueAsTree();
            if (detailsNode != null && detailsNode.size() != 0) {
                details = mapper.writeValueAsString(detailsNode);
            }

        } catch (Exception e) {
            throw new CommunicationContext.JsonResponseInvalidException("Deserialization of incoming error message failed", e, responseContext);
        }

        OcppJsonError error = new OcppJsonError();
        error.setMessageId(messageId);
        error.setErrorCode(code);
        error.setErrorDescription(desc);
        error.setErrorDetails(details);

        return new CommunicationContext.InError(inMsg, error, responseContext);
    }

    private static String getDetails(Exception e) {
        if (e instanceof ConstraintViolationException || e.getCause() instanceof ConstraintViolationException) {
            return "Violation of field constraints";
        }
        if (e instanceof DatabindException) {
            return "Invalid payload value (cannot understand one field)";
        }
        return null;
    }

    /**
     * Ensures incoming responses map only to active, non-stale calls.
     * Unknown or expired correlations are rejected to prevent accidental matching.
     */
    private static void validate(FutureResponseContext frc) throws CommunicationContext.JsonResponseInvalidException {
        if (frc == null) {
            throw new CommunicationContext.JsonResponseInvalidException("A response message was received to a not-sent call", null);
        }

        if (frc.hasTimedOut(Instant.now())) {
            // carry frc with this exception, because IncomingPipeline will need it to handle and propagate.
            throw new CommunicationContext.JsonResponseInvalidException("A response message was received to an expired call", frc);
        }
    }
}

/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.MessageType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Outgoing OcppJsonMessage --> String.
 *
 * This class should remain stateless.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
@Slf4j
public enum Serializer implements Consumer<CommunicationContext> {
    INSTANCE;

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

    @Override
    public void accept(CommunicationContext context) {
        OcppJsonMessage message = context.getOutgoingMessage();

        ArrayNode str;
        MessageType messageType = message.getMessageType();
        switch (messageType) {
            case CALL:
                str = handleCall((OcppJsonCall) message);
                break;

            case CALL_RESULT:
                str = handleResult((OcppJsonResult) message);
                break;

            case CALL_ERROR:
                str = handleError((OcppJsonError) message);
                break;

            default:
                throw new SteveException("Unknown enum type");
        }

        try {
            String result = mapper.writeValueAsString(str);
            context.setOutgoingString(result);
        } catch (IOException e) {
            throw new SteveException("The outgoing message could not be serialized", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private Helpers
    // -------------------------------------------------------------------------

    /**
     * Do NOT catch and handle exceptions for outgoing CALLs. Do NOT send the message.
     * Let the processing fail and acknowledge the user.
     */
    private ArrayNode handleCall(OcppJsonCall call) {
        JsonNode payloadNode;
        try {
            payloadNode = mapper.valueToTree(call.getPayload());
        } catch (IllegalArgumentException e) {
            throw new SteveException("The payload of the outgoing call could not be converted to JSON", e);
        }

        return mapper.createArrayNode()
                     .add(call.getMessageType().getTypeNr())
                     .add(call.getMessageId())
                     .add(call.getAction())
                     .add(payloadNode);
    }

    /**
     * Catch exceptions and wrap them in outgoing ERRORs for outgoing RESPONSEs.
     */
    private ArrayNode handleResult(OcppJsonResult result) {
        JsonNode payloadNode;
        try {
            payloadNode = mapper.valueToTree(result.getPayload());
        } catch (IllegalArgumentException e) {
            log.error("Exception occurred", e);
            return handleError(ErrorFactory.payloadSerializeError(result.getMessageId(), e.getMessage()));
        }

        return mapper.createArrayNode()
                     .add(result.getMessageType().getTypeNr())
                     .add(result.getMessageId())
                     .add(payloadNode);
    }

    /**
     * No exception to catch during serialization, since the fields of the error are simple Strings.
     */
    private ArrayNode handleError(OcppJsonError error) {
        // From spec:
        // ErrorDescription - Should be filled in if possible, otherwise a clear empty string "".
        String description;
        if (error.isSetDescription()) {
            description = error.getErrorDescription();
        } else {
            description = "";
        }

        // From spec:
        // ErrorDetails - This JSON object describes error details in an undefined way.
        // If there are no error details you should fill in an empty object {}, missing or null is not allowed
        ObjectNode detailsNode = mapper.createObjectNode();
        if (error.isSetDetails()) {
            detailsNode.set("errorMsg", mapper.getNodeFactory().textNode(error.toStringErrorDetails()));
        }

        return mapper.createArrayNode()
                     .add(error.getMessageType().getTypeNr())
                     .add(error.getMessageId())
                     .add(error.getErrorCode().name())
                     .add(description)
                     .add(detailsNode);
    }
}

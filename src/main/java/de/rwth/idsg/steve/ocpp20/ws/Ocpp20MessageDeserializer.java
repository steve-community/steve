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
package de.rwth.idsg.steve.ocpp20.ws;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class Ocpp20MessageDeserializer extends JsonDeserializer<Ocpp20Message> {

    @Override
    public Ocpp20Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (!node.isArray()) {
            throw new IllegalArgumentException("OCPP 2.0 message must be a JSON array");
        }

        if (node.size() < 3) {
            throw new IllegalArgumentException("OCPP 2.0 message array must have at least 3 elements");
        }

        JsonNode messageTypeNode = node.get(0);
        if (!messageTypeNode.isNumber()) {
            throw new IllegalArgumentException("Message type ID must be a number");
        }

        JsonNode messageIdNode = node.get(1);
        if (!messageIdNode.isTextual()) {
            throw new IllegalArgumentException("Message ID must be a string");
        }

        int messageTypeId = messageTypeNode.asInt();
        String messageId = messageIdNode.asText();

        Ocpp20Message message = new Ocpp20Message();
        message.setMessageTypeId(messageTypeId);
        message.setMessageId(messageId);

        if (messageTypeId == 2) {
            if (node.size() != 4) {
                throw new IllegalArgumentException("Call message must have exactly 4 elements");
            }

            JsonNode actionNode = node.get(2);
            if (!actionNode.isTextual()) {
                throw new IllegalArgumentException("Action must be a string");
            }

            message.setAction(actionNode.asText());
            message.setPayload(node.get(3));

        } else if (messageTypeId == 3) {
            if (node.size() != 3) {
                throw new IllegalArgumentException("CallResult message must have exactly 3 elements");
            }

            message.setPayload(node.get(2));

        } else if (messageTypeId == 4) {
            if (node.size() < 4 || node.size() > 5) {
                throw new IllegalArgumentException("CallError message must have 4 or 5 elements");
            }

            JsonNode errorCodeNode = node.get(2);
            if (!errorCodeNode.isTextual()) {
                throw new IllegalArgumentException("Error code must be a string");
            }

            message.setAction(errorCodeNode.asText());

            JsonNode errorDescriptionNode = node.get(3);
            if (!errorDescriptionNode.isTextual()) {
                throw new IllegalArgumentException("Error description must be a string");
            }

            JsonNode errorDetailsNode = node.size() > 4 ? node.get(4) : null;

            JsonNode combinedPayload = ctxt.getNodeFactory().objectNode()
                .put("errorCode", errorCodeNode.asText())
                .put("errorDescription", errorDescriptionNode.asText())
                .set("errorDetails", errorDetailsNode);

            message.setPayload(combinedPayload);

        } else {
            throw new IllegalArgumentException("Unsupported message type ID: " + messageTypeId);
        }

        return message;
    }
}
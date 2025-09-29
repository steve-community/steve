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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Ocpp20Message {

    private int messageTypeId;
    private String messageId;
    private String action;
    private JsonNode payload;

    public Ocpp20Message() {
    }

    public Ocpp20Message(int messageTypeId, String messageId) {
        this.messageTypeId = messageTypeId;
        this.messageId = messageId;
    }

    public Ocpp20Message(int messageTypeId, String messageId, String action, JsonNode payload) {
        this.messageTypeId = messageTypeId;
        this.messageId = messageId;
        this.action = action;
        this.payload = payload;
    }

    public boolean isCall() {
        return messageTypeId == 2;
    }

    public boolean isCallResult() {
        return messageTypeId == 3;
    }

    public boolean isCallError() {
        return messageTypeId == 4;
    }

    public void validate() throws IllegalArgumentException {
        if (messageTypeId < 2 || messageTypeId > 4) {
            throw new IllegalArgumentException("Invalid message type ID: " + messageTypeId);
        }

        if (messageId == null || messageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Message ID cannot be null or empty");
        }

        if (messageId.length() > 36) {
            throw new IllegalArgumentException("Message ID too long (max 36 characters): " + messageId.length());
        }

        if (isCall() && (action == null || action.trim().isEmpty())) {
            throw new IllegalArgumentException("Action cannot be null or empty for Call messages");
        }

        if (isCall() && action.length() > 50) {
            throw new IllegalArgumentException("Action too long (max 50 characters): " + action.length());
        }

        if (isCall() && payload == null) {
            throw new IllegalArgumentException("Payload cannot be null for Call messages");
        }
    }
}
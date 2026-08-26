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
package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Default holder/context of incoming and outgoing messages.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2015
 */
public class CommunicationContext {

    // -------------------------------------------------------------------------
    // Raw messages. These are queue-friendly and can be serialized
    // -------------------------------------------------------------------------

    public record In(
        String chargeBoxId,
        OcppProtocol protocol,
        String webSocketSessionId,

        String ocppPayload // full and raw JSON array payload
    ) {
    }

    /**
     * For responses (result or error) to incoming requests
     */
    public record Out(
        String chargeBoxId,
        OcppProtocol protocol,
        String webSocketSessionId,

        String ocppPayload // full and raw JSON array payload
    ) {
    }

    /**
     * Outgoing requests (calls)
     */
    public record OutCall(
        String chargeBoxId,
        OcppProtocol protocol,
        UUID taskUuid,

        String ocppPayload, // full and raw JSON array payload
        String ocppAction,
        String ocppMessageId
    ) {
    }

    public static Out outFrom(CommunicationContext.In in, String ocppPayload) {
        return new Out(
            in.chargeBoxId,
            in.protocol,
            in.webSocketSessionId,
            ocppPayload
        );
    }

    // -------------------------------------------------------------------------
    // In-process data holders
    // -------------------------------------------------------------------------

    public sealed interface DeserializationResult {

    }

    public record InCall(
        In in,
        OcppJsonCall call
    ) implements DeserializationResult {
    }

    public record InResult(
        In in,
        OcppJsonResult result,
        FutureResponseContext frc
    ) implements DeserializationResult {
    }

    public record InError(
        In in,
        OcppJsonError error,
        FutureResponseContext frc
    ) implements DeserializationResult {
    }

    // -------------------------------------------------------------------------
    // Custom exceptions
    // -------------------------------------------------------------------------

    public static class JsonCallParseException extends Exception {

        @Getter
        private final OcppJsonError parseError;

        public JsonCallParseException(OcppJsonError parseError) {
            this.parseError = parseError;
        }
    }

    public static class JsonResponseInvalidException extends Exception {

        @Getter
        private final FutureResponseContext frc;

        public JsonResponseInvalidException(String msg, @Nullable FutureResponseContext frc) {
            super(msg);
            this.frc = frc;
        }

        public JsonResponseInvalidException(String msg, Throwable cause, @Nullable FutureResponseContext frc) {
            super(msg, cause);
            this.frc = frc;
        }
    }
}

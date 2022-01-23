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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;

/**
 * Error generation should be handled by a central component for better control over the codes and messages.
 * This class just does that.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
public final class ErrorFactory {
    private ErrorFactory() { }

    public static OcppJsonError genericDeserializeError(String messageId, String details) {
        return setFields(messageId, ErrorCode.GenericError,
                "The incoming string could not be parsed into an array. Is the JSON syntactically correct?", details);
    }

    public static OcppJsonError payloadDeserializeError(String messageId, String details) {
        return setFields(messageId, ErrorCode.FormationViolation,
                "The payload for action could not be deserialized", details);
    }

    public static OcppJsonError payloadSerializeError(String messageId, String details) {
        return setFields(messageId, ErrorCode.InternalError,
                "The payload for action could not be serialized", details);
    }

    public static OcppJsonError actionNotFound(String messageId, String action) {
        return setFields(messageId, ErrorCode.NotImplemented,
                "The action '" + action + "' you are looking for is not found", null);
    }

    public static OcppJsonError payloadProcessingError(String messageId, String details) {
        return setFields(messageId, ErrorCode.InternalError,
                "Internal services failed while processing of the payload", details);
    }

    private static OcppJsonError setFields(String messageId, ErrorCode code, String desc, String details) {
        OcppJsonError error = new OcppJsonError();
        error.setMessageId(messageId);
        error.setErrorCode(code);
        error.setErrorDescription(desc);
        error.setErrorDetails(details);
        return error;
    }

}

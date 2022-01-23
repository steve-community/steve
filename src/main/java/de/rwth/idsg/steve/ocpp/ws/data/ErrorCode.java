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
package de.rwth.idsg.steve.ocpp.ws.data;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.03.2015
 */
public enum ErrorCode {
    // Requested Action is not known by receiver
    NotImplemented,
    // Requested Action is recognized but not supported by the receiver
    NotSupported,
    // An internal error occurred and the receiver was not able to process the requested Action successfully
    InternalError,
    // Payload for Action is incomplete
    ProtocolError,
    // During the processing of Action a security issue occurred preventing receiver from completing the
    // Action successfully
    SecurityError,
    // Payload for Action is syntactically incorrect or not conform the PDU structure for Action
    FormationViolation,
    // Payload is syntactically correct but at least one field contains an invalid value
    PropertyConstraintViolation,
    // Payload for Action is syntactically correct but at least one of the fields violates occurence constraints
    OccurenceConstraintViolation,
    // Payload for Action is syntactically correct but at least one of the fields violates data type constraints
    // (e.g. “somestring”: 12)
    TypeConstraintViolation,
    // Any other error not covered by the previous ones
    GenericError;

    public static ErrorCode fromValue(String str) {
        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.name().equals(str)) {
                return ec;
            }
        }
        throw new IllegalArgumentException(str + " is an unknown error code");
    }

    @Override
    public String toString() {
        return super.name();
    }
}

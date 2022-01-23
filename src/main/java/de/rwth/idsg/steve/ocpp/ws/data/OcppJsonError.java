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

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2015
 */
@Getter
@Setter
public class OcppJsonError extends OcppJsonResponse {
    private ErrorCode errorCode;
    private String errorDescription;
    private String errorDetails;

    public OcppJsonError() {
        super(MessageType.CALL_ERROR);
    }

    public boolean isSetDescription() {
        return errorDescription != null;
    }

    public boolean isSetDetails() {
        return errorDetails != null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Code: ").append(errorCode.toString());

        if (isSetDescription()) {
            b.append(", Description: ").append(errorDescription);
        }

        if (isSetDetails()) {
            b.append(", Details: ").append(errorDetails);
        }

        return b.toString();
    }

    /**
     * Let's be defensive and prevent problems due to errorDetails
     * that can be longer than what is expected/allowed on the receiving end.
     *
     * It actually happened with a charge point which responded with the message
     * "ERROR: dropped too large packet" and the connection was closed.
     */
    public String toStringErrorDetails() {
        if (errorDetails.length() > 100) {
            return errorDetails.substring(0, 100) + "...";
        } else {
            return errorDetails;
        }
    }
}

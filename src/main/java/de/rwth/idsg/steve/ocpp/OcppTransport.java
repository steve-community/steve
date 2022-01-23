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
package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.03.2015
 */
@RequiredArgsConstructor
@Getter
public enum OcppTransport {
    SOAP("S"),  // HTTP with SOAP payloads
    JSON("J");  // WebSocket with JSON payloads

    // The value should always contain ONE character!
    // Otherwise, it will break OcppProtocol.fromCompositeValue()
    //
    private final String value;

    public static OcppTransport fromValue(String v) {
        for (OcppTransport c: OcppTransport.values()) {
            if (c.getValue().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static OcppTransport fromName(String v) {
        for (OcppTransport c: OcppTransport.values()) {
            if (c.name().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

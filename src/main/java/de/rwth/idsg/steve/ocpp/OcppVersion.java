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
 * The values are as defined in spec "OCPP implementation guide SOAP - RC1 0.6" and in section "5. OCPP version"
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.12.2014
 */
@RequiredArgsConstructor
@Getter
public enum OcppVersion {
    V_12("ocpp1.2"),
    V_15("ocpp1.5"),
    V_16("ocpp1.6");

    private final String value;

    public static OcppVersion fromValue(String v) {
        for (OcppVersion c: OcppVersion.values()) {
            if (c.getValue().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public OcppProtocol toProtocol(OcppTransport transport) {
        for (OcppProtocol value : OcppProtocol.values()) {
            if (value.getVersion() == this && value.getTransport() == transport) {
                return value;
            }
        }
        throw new IllegalArgumentException("Could not find OcppProtocol for " + transport);
    }
}

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
public enum OcppProtocol {
    V_12_SOAP(OcppVersion.V_12, OcppTransport.SOAP),
    V_12_JSON(OcppVersion.V_12, OcppTransport.JSON),

    V_15_SOAP(OcppVersion.V_15, OcppTransport.SOAP),
    V_15_JSON(OcppVersion.V_15, OcppTransport.JSON),

    V_16_SOAP(OcppVersion.V_16, OcppTransport.SOAP),
    V_16_JSON(OcppVersion.V_16, OcppTransport.JSON);

    private final OcppVersion version;
    private final OcppTransport transport;

    public String getCompositeValue() {
        return version.getValue() + transport.getValue();
    }

    public static OcppProtocol fromCompositeValue(String v) {

        // If we, in future, decide to use values
        // containing more than one character for OcppTransport,
        // this will break.
        //
        int splitIndex = v.length() - 1;

        String version = v.substring(0, splitIndex);
        String transport = String.valueOf(v.charAt(splitIndex));

        OcppVersion ov = OcppVersion.fromValue(version);
        OcppTransport ot = OcppTransport.fromValue(transport);

        for (OcppProtocol c: OcppProtocol.values()) {
            if (c.getVersion() == ov && c.getTransport() == ot) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

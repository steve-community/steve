/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OcppVersionTest {

    @ParameterizedTest
    @EnumSource(OcppVersion.class)
    public void testFromValue(OcppVersion input) {
        String toTest = input.getValue();
        OcppVersion inputBack = OcppVersion.fromValue(toTest);
        Assertions.assertEquals(input, inputBack);
    }

    @ParameterizedTest
    @EnumSource(OcppTransport.class)
    public void testToProtocol(OcppTransport transport) {
        for (OcppVersion version : OcppVersion.values()) {
            OcppProtocol protocol = version.toProtocol(transport);

            Assertions.assertEquals(transport, protocol.getTransport());
            Assertions.assertEquals(version, protocol.getVersion());
        }
    }
}

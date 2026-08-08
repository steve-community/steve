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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConnectorStatusFilterTest {

    @Test
    public void testZero_onlyZero() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(0).build();

        List<ConnectorStatus> list = List.of(cs0);
        var result = ConnectorStatusFilter.filterAndPreferZero(list);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(cs0));
    }

    @Test
    public void testZero_noZero() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs1, cs2);
        var result = ConnectorStatusFilter.filterAndPreferZero(list);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cs1));
        Assertions.assertTrue(result.contains(cs2));
    }

    @Test
    public void testZero_zeroIsEarlier() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.minusHours(1)).chargeBoxId("foo").status("Available").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.filterAndPreferZero(list);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cs1));
        Assertions.assertTrue(result.contains(cs2));
    }

    @Test
    public void testZero_zeroIsLater() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.plusHours(1)).chargeBoxId("foo").status("Available").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.filterAndPreferZero(list);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(cs0));
    }

    /**
     * https://github.com/steve-community/steve/issues/1906
     */
    @Test
    public void testZero_sameTimestamp() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.filterAndPreferZero(list);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cs1));
        Assertions.assertTrue(result.contains(cs2));
    }
}

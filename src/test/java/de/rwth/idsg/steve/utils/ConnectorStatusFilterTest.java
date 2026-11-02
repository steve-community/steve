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
import java.util.Set;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.ConnectorStatusFilter.Strategy.AsReported;
import static de.rwth.idsg.steve.utils.ConnectorStatusFilter.Strategy.InheritFromStation;
import static de.rwth.idsg.steve.utils.ConnectorStatusFilter.Strategy.ReplaceWithStation;

public class ConnectorStatusFilterTest {

    @Test
    public void testZero_onlyZero() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(0).build();

        List<ConnectorStatus> list = List.of(cs0);
        var result = ConnectorStatusFilter.processAndFilterList(list, ReplaceWithStation);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(cs0));
    }

    @Test
    public void testZero_noZero() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs1, cs2);
        var result = ConnectorStatusFilter.processAndFilterList(list, ReplaceWithStation);

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
        var result = ConnectorStatusFilter.processAndFilterList(list, ReplaceWithStation);

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
        var result = ConnectorStatusFilter.processAndFilterList(list, ReplaceWithStation);

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
        var result = ConnectorStatusFilter.processAndFilterList(list, ReplaceWithStation);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cs1));
        Assertions.assertTrue(result.contains(cs2));
    }

    @Test
    public void testInherit_zeroIsLater() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.plusHours(1)).chargeBoxId("foo").status("Faulted").errorCode("OtherError").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Charging").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.processAndFilterList(list, InheritFromStation);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(Set.of(1, 2), result.stream().map(ConnectorStatus::getConnectorId).collect(Collectors.toSet()));
        for (ConnectorStatus cs : result) {
            Assertions.assertEquals("Faulted", cs.getStatus());
            Assertions.assertEquals("OtherError", cs.getErrorCode());
            Assertions.assertEquals(cs0.getStatusTimestamp(), cs.getStatusTimestamp());
        }
    }

    @Test
    public void testInherit_zeroIsEarlier() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.minusHours(1)).chargeBoxId("foo").status("Faulted").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Charging").connectorId(2).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.processAndFilterList(list, InheritFromStation);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cs1));
        Assertions.assertTrue(result.contains(cs2));
    }

    @Test
    public void testAsReported_zeroIsLater() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.plusHours(1)).chargeBoxId("foo").status("Faulted").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs2 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("bar").status("Charging").connectorId(1).build();

        List<ConnectorStatus> list = List.of(cs0, cs1, cs2);
        var result = ConnectorStatusFilter.processAndFilterList(list, AsReported);

        Assertions.assertEquals(list, result);
    }

    @Test
    public void testAsReported_zeroIsEarlier() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt.minusHours(1)).chargeBoxId("foo").status("Faulted").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();

        List<ConnectorStatus> list = List.of(cs0, cs1);
        var result = ConnectorStatusFilter.processAndFilterList(list, AsReported);

        Assertions.assertEquals(list, result);
    }

    @Test
    public void testAsReported_sameTimestamp() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Faulted").connectorId(0).build();
        ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();

        List<ConnectorStatus> list = List.of(cs0, cs1);
        var result = ConnectorStatusFilter.processAndFilterList(list, AsReported);

        Assertions.assertEquals(list, result);
    }

    /**
     * Two rows with the same latest timestamp for one connector can come back from the DB. AsReported keeps both.
     */
    @Test
    public void testAsReported_duplicateLatest() {
        DateTime dt = DateTime.parse("2025-12-10T12:00:00.000Z");

        ConnectorStatus cs1a = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Available").connectorId(1).build();
        ConnectorStatus cs1b = ConnectorStatus.builder().statusTimestamp(dt).chargeBoxId("foo").status("Preparing").connectorId(1).build();

        List<ConnectorStatus> list = List.of(cs1a, cs1b);
        var result = ConnectorStatusFilter.processAndFilterList(list, AsReported);

        Assertions.assertEquals(list, result);
    }
}

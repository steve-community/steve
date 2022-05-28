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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2016
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectorStatusCountFilter {

    public static final Set<String> ALL_STATUS_VALUES = allStatusValues();

    public static Map<String, Integer> getStatusCountMap(List<ConnectorStatus> latestList) {
        return getStatusCountMap(latestList, false);
    }

    public static Map<String, Integer> getStatusCountMap(List<ConnectorStatus> latestList, boolean printZero) {
        List<ConnectorStatus> filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);

        // TreeMap because we want a consistent order of the listing on the page
        TreeMap<String, Integer> map = new TreeMap<>();
        for (ConnectorStatus item : filteredList) {
            Integer count = map.get(item.getStatus());
            if (count == null) {
                count = 1;
            } else {
                count += 1;
            }
            map.put(item.getStatus(), count);
        }

        if (printZero) {
            ALL_STATUS_VALUES.forEach(s -> map.putIfAbsent(s, 0));
        }

        return map;
    }

    private static Set<String> allStatusValues() {
        // to have a predictable sorting on the web page
        TreeSet<String> set = new TreeSet<>(Comparator.naturalOrder());

        EnumSet.allOf(ocpp.cs._2010._08.ChargePointStatus.class).forEach(k -> set.add(k.value()));
        EnumSet.allOf(ocpp.cs._2012._06.ChargePointStatus.class).forEach(k -> set.add(k.value()));
        EnumSet.allOf(ocpp.cs._2015._10.ChargePointStatus.class).forEach(k -> set.add(k.value()));

        return set;
    }

}

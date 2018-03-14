package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2016
 */
public final class ConnectorStatusCountFilter {

    private static final Set<String> allStatusValues = allStatusValues();

    private ConnectorStatusCountFilter() { }

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
            allStatusValues.forEach(s -> map.putIfAbsent(s, 0));
        }

        return map;
    }

    private static Set<String> allStatusValues() {
        HashSet<String> set = new HashSet<>();
        EnumSet.allOf(ocpp.cs._2010._08.ChargePointStatus.class).forEach(k -> set.add(k.value()));
        EnumSet.allOf(ocpp.cs._2012._06.ChargePointStatus.class).forEach(k -> set.add(k.value()));
        EnumSet.allOf(ocpp.cs._2015._10.ChargePointStatus.class).forEach(k -> set.add(k.value()));
        return set;
    }

}

package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import ocpp.cs._2012._06.ChargePointStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ocpp.cs._2012._06.ChargePointStatus.AVAILABLE;
import static ocpp.cs._2012._06.ChargePointStatus.FAULTED;
import static ocpp.cs._2012._06.ChargePointStatus.OCCUPIED;
import static ocpp.cs._2012._06.ChargePointStatus.RESERVED;
import static ocpp.cs._2012._06.ChargePointStatus.UNAVAILABLE;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2016
 */
public final class ConnectorStatusCountFilter {

    private ConnectorStatusCountFilter() { }

    /**
     * ChargePointStatus in Ocpp 1.5 is a super set of the one in Ocpp 1.2. Therefore,
     * using Ocpp 1.5 will cover 1.2 too.
     */
    public static Map<ChargePointStatus, Integer> getStatusCountMap(List<ConnectorStatus> latestList) {
        List<ConnectorStatus> filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);

        int countAvailable = 0;
        int countOccupied = 0;
        int countFaulted = 0;
        int countUnavailable = 0;
        int countReserved = 0;

        for (ConnectorStatus cs : filteredList) {
            ChargePointStatus cps = ChargePointStatus.fromValue(cs.getStatus());
            switch (cps) {
                case AVAILABLE:
                    countAvailable++;
                    break;

                case OCCUPIED:
                    countOccupied++;
                    break;

                case FAULTED:
                    countFaulted++;
                    break;

                case UNAVAILABLE:
                    countUnavailable++;
                    break;

                case RESERVED:
                    countReserved++;
                    break;

                default:
                    break;
            }
        }

        // LinkedHashMap because we want a consistent order of the listing on the page
        Map<ChargePointStatus, Integer> tmp = new LinkedHashMap<>(5);
        tmp.put(AVAILABLE, countAvailable);
        tmp.put(OCCUPIED, countOccupied);
        tmp.put(RESERVED, countReserved);
        tmp.put(UNAVAILABLE, countUnavailable);
        tmp.put(FAULTED, countFaulted);
        return tmp;
    }
}

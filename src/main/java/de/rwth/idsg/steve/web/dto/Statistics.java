package de.rwth.idsg.steve.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ocpp.cs._2012._06.ChargePointStatus;

import java.util.Map;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class Statistics {
    // Number of chargeboxes, ocppTags, users, reservations, transactions
    private final Integer numChargeBoxes, numOcppTags, numUsers, numReservations, numTransactions,
    // Received heartbeats
    heartbeatToday, heartbeatYesterday, heartbeatEarlier;

    // Number of connected WebSocket/JSON chargeboxes
    @Setter private int numOcpp12JChargeBoxes, numOcpp15JChargeBoxes;

    // Count of connectors based on their status
    @Setter private Map<ChargePointStatus, Integer> statusCountMap;
}

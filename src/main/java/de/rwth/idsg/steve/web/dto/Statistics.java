package de.rwth.idsg.steve.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class Statistics {
    // Number of chargeboxes, users, reservations, transactions
    private final Integer numChargeBoxes, numUsers, numReservations, numTransactions,
    // Received heartbeats
    heartbeatToday, heartbeatYesterday, heartbeatEarlier,
    // Number of available, occupied, faulted and unavailable chargebox connectors
    connAvailable, connOccupied, connFaulted, connUnavailable;

    // Number of connected WebSocket/JSON chargeboxes
    @Setter private int numOcpp12JChargeBoxes, numOcpp15JChargeBoxes;
}

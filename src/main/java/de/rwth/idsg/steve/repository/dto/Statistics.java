package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@ToString
@Getter
@Builder
public final class Statistics {
    // Number of chargeboxes, users, reservations, transactions
    private final Integer numChargeBoxes, numUsers, numReservations, numTransactions,
    // Received heartbeats
    heartbeatToday, heartbeatYesterday, heartbeatEarlier,
    // Number of available, occupied, faulted and unavailable chargebox connectors
    connAvailable, connOccupied, connFaulted, connUnavailable;
}
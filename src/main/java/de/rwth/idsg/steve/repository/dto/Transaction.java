package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class Transaction {
    private final int id, connectorId;
    private final String chargeBoxId, idTag, startTimestamp, startValue, stopTimestamp, stopValue;
}

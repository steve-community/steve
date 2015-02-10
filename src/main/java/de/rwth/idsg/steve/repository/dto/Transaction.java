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
    private int id, connectorId;
    private String chargeBoxId, idTag, startTimestamp, startValue, stopTimestamp, stopValue;
}
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
    private final int id, connectorId, chargeBoxPk, ocppTagPk;
    private final String chargeBoxId, ocppIdTag, startTimestamp, startValue, stopTimestamp, stopValue;
}

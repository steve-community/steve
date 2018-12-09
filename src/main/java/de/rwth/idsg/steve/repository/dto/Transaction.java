package de.rwth.idsg.steve.repository.dto;

import jooq.steve.db.enums.TransactionStopEventActor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class Transaction {
    private final int id, connectorId, chargeBoxPk, ocppTagPk;
    private final String chargeBoxId, ocppIdTag, startTimestamp, startValue;
    private final DateTime startTimestampDT;

    @Nullable private final String stopTimestamp;
    @Nullable private final String stopValue;
    @Nullable private final String stopReason; // new in OCPP 1.6
    @Nullable private final DateTime stopTimestampDT;
    @Nullable private final TransactionStopEventActor stopEventActor;
}

package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2016
 */
@Getter
@Builder
public class UpdateTransactionParams {
    private final String chargeBoxId;
    private final int transactionId;
    private final DateTime stopTimestamp;
    private final String stopMeterValue;

    private final TransactionStatusUpdate statusUpdate = TransactionStatusUpdate.AfterStop;
}

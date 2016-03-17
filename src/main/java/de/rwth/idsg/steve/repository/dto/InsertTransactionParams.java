package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.11.2015
 */
@Getter
@Builder
public class InsertTransactionParams {
    private final String chargeBoxId;
    private final int connectorId;
    private final String idTag;
    private final DateTime startTimestamp;
    private final String startMeterValue;

    private final TransactionStatusUpdate statusUpdate = TransactionStatusUpdate.AfterStart;

    // Only in OCPP1.5
    private final Integer reservationId;

    public boolean isSetReservationId() {
        return reservationId != null;
    }

}

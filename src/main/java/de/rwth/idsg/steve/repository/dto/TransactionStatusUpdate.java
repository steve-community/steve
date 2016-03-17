package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import ocpp.cs._2012._06.ChargePointErrorCode;
import ocpp.cs._2012._06.ChargePointStatus;

import static ocpp.cs._2012._06.ChargePointStatus.AVAILABLE;
import static ocpp.cs._2012._06.ChargePointStatus.OCCUPIED;

/**
 * 1) Exists only to ensure type safety
 * 2) Does not matter, whether we use ChargePointStatus and ChargePointErrorCode from OCPP 1.2 or 1.5
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2016
 */
@Getter
public enum TransactionStatusUpdate {
    AfterStart(OCCUPIED),
    AfterStop(AVAILABLE);

    private final String status;
    private final String errorCode = ChargePointErrorCode.NO_ERROR.value();

    TransactionStatusUpdate(ChargePointStatus status) {
        this.status = status.value();
    }
}

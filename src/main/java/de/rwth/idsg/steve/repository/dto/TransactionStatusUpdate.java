package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;

/**
 * Exists only to ensure type safety
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2016
 */
@Getter
public enum TransactionStatusUpdate {

    AfterStart(ChargePointStatus.CHARGING),
    AfterStop(ChargePointStatus.AVAILABLE);

    private final String status;
    private final String errorCode = ChargePointErrorCode.NO_ERROR.value();

    TransactionStatusUpdate(ChargePointStatus status) {
        this.status = status.value();
    }
}

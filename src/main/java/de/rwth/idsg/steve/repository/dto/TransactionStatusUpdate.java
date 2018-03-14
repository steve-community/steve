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

    // From OCPP 1.6: When the contactor of a Connector closes, allowing the vehicle to charge
    AfterStart(ChargePointStatus.CHARGING),

    // From OCPP 1.6: When a charging session has stopped at a Connector, but the Connector is not yet available
    // for a new user, e.g. the cable has not been removed or the vehicle has not left the parking bay
    AfterStop(ChargePointStatus.FINISHING);

    private final String status;
    private final String errorCode = ChargePointErrorCode.NO_ERROR.value();

    TransactionStatusUpdate(ChargePointStatus status) {
        this.status = status.value();
    }
}

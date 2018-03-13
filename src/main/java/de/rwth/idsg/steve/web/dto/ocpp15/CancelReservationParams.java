package de.rwth.idsg.steve.web.dto.ocpp15;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@Getter
@Setter
public class CancelReservationParams extends SingleChargePointSelect {

    @NotNull(message = "Reservation ID is required")
    @Min(value = 0, message = "Reservation ID must be at least {value}")
    private Integer reservationId;
}

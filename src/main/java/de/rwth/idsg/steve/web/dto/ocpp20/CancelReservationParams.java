package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelReservationParams extends BaseParams {

    @NotNull(message = "Reservation ID is required")
    private Integer reservationId;
}
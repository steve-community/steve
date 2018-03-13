/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author david
 */
@Getter
@Setter
public class CancelReservationParams extends SingleChargePointSelect 
{
    @NotNull(message = "Reservation ID is required")
    @Min(value = 0, message = "Reservation ID must be at least {value}")
    private Integer reservationId;
}

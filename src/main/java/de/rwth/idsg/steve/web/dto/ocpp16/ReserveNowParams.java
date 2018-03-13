/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author david
 */

@Getter
@Setter
public class ReserveNowParams extends SingleChargePointSelect 
{
    @NotNull(message = "Connector ID is required")
    @Min(value = 1, message = "Connector ID must be at least {value}")
    private Integer connectorId;
    
    @NotNull(message = "Expiry Date/Time is required")
    @Future(message = "Expiry Date/Time must be in future")
    private LocalDateTime expiry;
    
    @NotBlank(message = "User ID Tag is required")
    @IdTag
    private String idTag;
}

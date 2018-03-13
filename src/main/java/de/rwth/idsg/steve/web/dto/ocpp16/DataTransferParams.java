/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 *
 * @author david
 */

@Getter
@Setter
public class DataTransferParams extends MultipleChargePointSelect 
{

    @NotNull(message = "Vendor ID is required")
    private String vendorId;

    private String messageId;

    private String data;
}

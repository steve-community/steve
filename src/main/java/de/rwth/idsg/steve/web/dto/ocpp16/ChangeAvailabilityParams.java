/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.AvailabilityType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author david
 */

@Getter
public class ChangeAvailabilityParams extends MultipleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotNull(message = "Availability Type is required")
    @Setter private AvailabilityType availType;

    /**
     * if empty, 0 = charge point as a whole
     */
    public void setConnectorId(Integer connectorId) {
        if (connectorId == null) {
            this.connectorId = 0;
        } else {
            this.connectorId = connectorId;
        }
    }
}

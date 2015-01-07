package de.rwth.idsg.steve.web.dto.ocpp15;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2012._06.AvailabilityType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
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

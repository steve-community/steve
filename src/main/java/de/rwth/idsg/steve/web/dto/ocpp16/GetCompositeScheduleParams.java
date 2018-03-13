package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingRateUnitType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class GetCompositeScheduleParams extends SingleChargePointSelect
{
    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotNull(message = "Duration required")
    private Integer duration;

    private ChargingRateUnitType chargingRateUnit;

    public boolean isSetChargingRateUnit() {return chargingRateUnit != null && !chargingRateUnit.equals(""); }

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

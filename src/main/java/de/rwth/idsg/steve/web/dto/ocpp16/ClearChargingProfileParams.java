package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfilePurposeType;

@Getter
@Setter
public class ClearChargingProfileParams extends SingleChargePointSelect
{
    private Integer id;

    private Integer connectorId;
    public boolean isSetConnectorId() {return connectorId != null && !connectorId.equals(""); }

    private ChargingProfilePurposeType chargingProfilePurpose;

    private Integer stackLevel;
}

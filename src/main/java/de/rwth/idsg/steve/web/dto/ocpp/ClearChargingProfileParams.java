package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfilePurposeType;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.11.2018
 */
@Getter
@Setter
public class ClearChargingProfileParams extends MultipleChargePointSelect {

    @Positive
    private Integer chargingProfilePk;

    // A connectorId of zero (0) specifies the charging profile for the overall Charge Point.
    // Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    private ChargingProfilePurposeType chargingProfilePurpose;

    private Integer stackLevel;

    /**
     * According to spec it's either chargingProfilePk or a combination of (connectorId, chargingProfilePurpose,
     * stackLevel).
     */
    @AssertTrue(message = "According to spec, either the chargingProfileId or a combination of (connectorId, chargingProfilePurpose, stackLevel) has to be set")
    public boolean isValid() {
        // if chargingProfilePk is set, others must be null.
        if (chargingProfilePk != null && connectorId != null && chargingProfilePurpose != null && stackLevel != null) {
            return false;
        }

        // if chargingProfilePk is not set, one of the others must be set.
        if (chargingProfilePk == null && connectorId == null && chargingProfilePurpose == null && stackLevel == null) {
            return false;
        }

        return true;
    }

}

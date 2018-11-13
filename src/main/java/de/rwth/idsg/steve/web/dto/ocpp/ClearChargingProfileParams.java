package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfilePurposeType;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.11.2018
 */
@Getter
@Setter
public class ClearChargingProfileParams extends MultipleChargePointSelect {

    @NotNull(message = "Filter Type is required")
    private ClearChargingProfileFilterType filterType = ClearChargingProfileFilterType.ChargingProfileId;

    @Positive
    private Integer chargingProfilePk;

    // A connectorId of zero (0) specifies the charging profile for the overall Charge Point.
    // Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request.
    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    private ChargingProfilePurposeType chargingProfilePurpose;

    private Integer stackLevel;

    @AssertTrue(message = "When filtering by id, charging profile id must be set")
    public boolean isValidWhenFilterById() {
        if (filterType == ClearChargingProfileFilterType.ChargingProfileId
                && chargingProfilePk == null) {
            return false;
        }
        return true;
    }

}

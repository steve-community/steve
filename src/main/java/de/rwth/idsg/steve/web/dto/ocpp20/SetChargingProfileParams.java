package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for OCPP 2.0 SetChargingProfile
 */
@Getter
@Setter
public class SetChargingProfileParams extends BaseParams {

    private Integer evseId;

    @NotNull
    private Integer profileId;

    @NotNull
    private Integer stackLevel;

    @NotNull
    private String profilePurpose; // ChargingLimitSource, TxDefaultProfile, TxProfile

    @NotNull
    private String profileKind; // Absolute, Recurring, Relative

    private String recurrencyKind; // Daily, Weekly

    private String validFrom;
    private String validTo;
    private String transactionId;

    // Simplified charging schedule for initial implementation
    @NotNull
    private Integer duration; // Duration in seconds

    private String startSchedule; // DateTime

    @NotNull
    private String chargingRateUnit; // W, A

    private Integer minChargingRate;

    // Schedule periods (simplified - just one period for now)
    @NotNull
    private Integer startPeriod = 0; // Start at second 0

    @NotNull
    private Double limit; // Power/current limit

    private Integer numberPhases = 3;
}
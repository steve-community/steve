package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.RecurrencyKindType;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.11.2018
 */
@Getter
@Setter
public class ChargingProfileForm {

    // Internal database id
    private Integer chargingProfilePk;

    private String description;
    private String note;

    @NotNull
    @PositiveOrZero
    private Integer stackLevel;

    @NotNull
    private ChargingProfilePurposeType chargingProfilePurpose;

    @NotNull
    private ChargingProfileKindType chargingProfileKind;

    private RecurrencyKindType recurrencyKind;

    @Future(message = "Valid From must be in future")
    private LocalDateTime validFrom;

    @Future(message = "Valid From must be in future")
    private LocalDateTime validTo;

    @Positive
    private Integer durationInSeconds;

    @Future(message = "Start schedule must be in future")
    private LocalDateTime startSchedule;

    @NotNull
    private ChargingRateUnitType chargingRateUnit;

    private BigDecimal minChargingRate;

//    @NotEmpty
    private List<SchedulePeriod> schedulePeriods;

    @AssertTrue(message = "Valid To must be after Valid From")
    public boolean isFromToValid() {
        return !(validFrom != null && validTo != null) || validTo.isAfter(validFrom);
    }

    @AssertTrue(message = "Start schedule must be between Valid To and From")
    public boolean isStartScheduleValid() {
        if (validFrom != null && startSchedule != null && !startSchedule.isAfter(validFrom)) {
            return false;
        }

        if (validTo != null && startSchedule != null && !startSchedule.isBefore(validTo)) {
            return false;
        }

        return true;
    }

    @AssertTrue(message = "Valid From/To should not be used with the profile purpose 'TxProfile'")
    public boolean isFromToAndProfileSettingCorrect() {
        boolean isTxProfile = (chargingProfilePurpose == ChargingProfilePurposeType.TX_PROFILE);

        if (validFrom != null && isTxProfile) {
            return false;
        }

        if (validTo != null && isTxProfile) {
            return false;
        }

        return true;
    }

    @Getter
    @Setter
    public static class SchedulePeriod {

        @NotNull
        private Integer startPeriodInSeconds; // from the startSchedule

        @NotNull
        private BigDecimal powerLimitInAmperes;

        private Integer numberPhases;
    }
}

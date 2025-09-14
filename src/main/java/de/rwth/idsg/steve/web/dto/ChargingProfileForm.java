/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.RecurrencyKindType;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
@Getter
@Setter
@ToString
public class ChargingProfileForm {

    // Internal database id
    private Integer chargingProfilePk;

    private String description;
    private String note;

    @NotNull(message = "Stack Level has to be set")
    @PositiveOrZero(message = "Stack Level has to be a positive number or 0")
    private Integer stackLevel;

    @NotNull(message = "Charging Profile Purpose has to be set")
    private ChargingProfilePurposeType chargingProfilePurpose;

    @NotNull(message = "Charging Profile Kind has to be set")
    private ChargingProfileKindType chargingProfileKind;

    private RecurrencyKindType recurrencyKind;

    private DateTime validFrom;

    @Future(message = "Valid To must be in future")
    private DateTime validTo;

    @Positive(message = "Duration has to be a positive number")
    private Integer durationInSeconds;

    private DateTime startSchedule;

    @NotNull(message = "Charging Rate Unit has to be set")
    private ChargingRateUnitType chargingRateUnit;

    private BigDecimal minChargingRate;

    private List<@Valid SchedulePeriod> schedulePeriods;

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

    @AssertTrue(message = "Schedule Periods cannot be empty")
    public boolean isSchedulePeriodsValid() {
        if (CollectionUtils.isEmpty(schedulePeriods)) {
            return false;
        }

        return schedulePeriods.stream()
            .filter(Objects::nonNull)
            .anyMatch(SchedulePeriod::isNonEmpty);
    }

    @Getter
    @Setter
    @ToString
    public static class SchedulePeriod {

        @Min(value = 0, message = "Start Period has to be a positive number or 0")
        private Integer startPeriodInSeconds; // from the startSchedule

        /**
         * According to spec: "Accepts at most one digit fraction (e.g. 8.1)"
         */
        @DecimalMin(value = "0.0", message = "Power Limit has to be a positive number or 0")
        @Digits(integer = 6, fraction = 1, message = "Power Limit must be a number with at most 6 digits and 1 fractional digit")
        private BigDecimal powerLimit;

        @Min(value = 1, message = "Number of Phases has to be at least 1")
        @Max(value = 3, message = "Number of Phases has to be at most 3")
        private Integer numberPhases;

        @AssertTrue(message = "Schedule period: Power Limit has to be set")
        public boolean isPowerLimitSet() {
            if (isEmpty()) {
                return true; // All fields are null, so validation is ignored
            }
            return powerLimit != null;
        }

        @AssertTrue(message = "Schedule period: Start Period has to be set")
        public boolean isStartPeriodInSecondsSet() {
            if (isEmpty()) {
                return true; // All fields are null, so validation is ignored
            }
            return startPeriodInSeconds != null;
        }

        public boolean isNonEmpty() {
            return !isEmpty();
        }

        private boolean isEmpty() {
            return (startPeriodInSeconds == null) && (powerLimit == null) && (numberPhases == null);
        }
    }
}

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
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
@Getter
@Setter
@ToString
public class ChargingProfileForm {

    public ChargingProfileForm() {}

    public static ChargingProfileForm fromDetails(de.rwth.idsg.steve.repository.dto.ChargingProfile.Details details) {
        var form = new ChargingProfileForm();
        form.chargingProfilePk = details.getChargingProfilePk();
        form.stackLevel = details.getStackLevel();
        form.chargingProfilePurpose = ChargingProfilePurposeType.fromValue(details.getChargingProfilePurpose());
        form.chargingProfileKind = ChargingProfileKindType.fromValue(details.getChargingProfileKind());
        form.recurrencyKind =
                details.getRecurrencyKind() == null ? null : RecurrencyKindType.fromValue(details.getRecurrencyKind());
        form.validFrom = details.getValidFrom();
        form.validTo = details.getValidTo();
        form.durationInSeconds = details.getDurationInSeconds();
        form.startSchedule = details.getStartSchedule();
        form.chargingRateUnit = ChargingRateUnitType.fromValue(details.getChargingRateUnit());
        form.minChargingRate = details.getMinChargingRate();
        form.schedulePeriods = details.getPeriods().stream()
                .map(p -> {
                    SchedulePeriod sp = new SchedulePeriod();
                    sp.setStartPeriodInSeconds(p.getStartPeriodInSeconds());
                    sp.setPowerLimit(p.getPowerLimit());
                    sp.setNumberPhases(p.getNumberPhases());
                    return sp;
                })
                .toList();
        return form;
    }

    // Internal database id
    private Integer chargingProfilePk;

    private String description;
    private String note;

    @NotNull(message = "Stack Level has to be set") @PositiveOrZero(message = "Stack Level has to be a positive number or 0") private Integer stackLevel;

    @NotNull(message = "Charging Profile Purpose has to be set") private ChargingProfilePurposeType chargingProfilePurpose;

    @NotNull(message = "Charging Profile Kind has to be set") private ChargingProfileKindType chargingProfileKind;

    private @Nullable RecurrencyKindType recurrencyKind;

    private Instant validFrom;

    @Future(message = "Valid To must be in future") private Instant validTo;

    @Positive(message = "Duration has to be a positive number") private Integer durationInSeconds;

    private Instant startSchedule;

    @NotNull(message = "Charging Rate Unit has to be set") private ChargingRateUnitType chargingRateUnit;

    private BigDecimal minChargingRate;

    @NotEmpty(message = "Schedule Periods cannot be empty") private List<@NotNull @Valid SchedulePeriod> schedulePeriods;

    @AssertTrue(message = "Valid To must be after Valid From") public boolean isFromToValid() {
        return !(validFrom != null && validTo != null) || validTo.isAfter(validFrom);
    }

    @AssertTrue(message = "Start schedule must be between Valid To and From") public boolean isStartScheduleValid() {
        if (validFrom != null && startSchedule != null && !startSchedule.isAfter(validFrom)) {
            return false;
        }

        if (validTo != null && startSchedule != null && !startSchedule.isBefore(validTo)) {
            return false;
        }

        return true;
    }

    @AssertTrue(message = "Valid From/To should not be used with the profile purpose 'TxProfile'") public boolean isFromToAndProfileSettingCorrect() {
        boolean isTxProfile = (chargingProfilePurpose == ChargingProfilePurposeType.TX_PROFILE);

        return (validFrom == null || !isTxProfile) && (validTo == null || !isTxProfile);
    }

    @Getter
    @Setter
    @ToString
    public static class SchedulePeriod {

        @NotNull @PositiveOrZero(message = "Start Period has to be a positive number or 0") private Integer startPeriodInSeconds; // from the startSchedule

        /**
         * According to spec: "Accepts at most one digit fraction (e.g. 8.1)"
         */
        @NotNull @PositiveOrZero(message = "Power Limit has to be a positive number or 0") @Digits(
                integer = 6,
                fraction = 1,
                message = "Power Limit must be a number with at most 6 digits and 1 fractional digit")
        private BigDecimal powerLimit;

        @Min(value = 1, message = "Number of Phases has to be at least 1") @Max(value = 3, message = "Number of Phases has to be at most 3") private Integer numberPhases;
    }
}

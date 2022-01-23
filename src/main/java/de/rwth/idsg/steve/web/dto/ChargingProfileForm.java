/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.RecurrencyKindType;
import org.joda.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
@Getter
@Setter
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

    private LocalDateTime validFrom;

    @Future(message = "Valid To must be in future")
    private LocalDateTime validTo;

    @Positive(message = "Duration has to be a positive number")
    private Integer durationInSeconds;

    private LocalDateTime startSchedule;

    @NotNull(message = "Charging Rate Unit has to be set")
    private ChargingRateUnitType chargingRateUnit;

    private BigDecimal minChargingRate;

    @NotEmpty(message = "Schedule Periods cannot be empty")
    @Valid
    private Map<String, SchedulePeriod> schedulePeriodMap;

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

        private static final int defaultNumberPhases = 3;

        @NotNull(message = "Schedule period: Start Period has to be set")
        private Integer startPeriodInSeconds; // from the startSchedule

        @NotNull(message = "Schedule period: Power Limit has to be set")
        private BigDecimal powerLimit;

        private Integer numberPhases;

        public Integer getNumberPhases() {
            return Objects.requireNonNullElse(numberPhases, defaultNumberPhases);
        }

        public void setNumberPhases(Integer numberPhases) {
            this.numberPhases = Objects.requireNonNullElse(numberPhases, defaultNumberPhases);
        }
    }
}

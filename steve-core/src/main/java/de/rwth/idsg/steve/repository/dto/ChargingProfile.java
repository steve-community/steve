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
package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
public class ChargingProfile {

    @Getter
    @RequiredArgsConstructor
    public static final class BasicInfo {
        private final int chargingProfilePk;
        private final String description;

        public String getItemDescription() {
            if (description == null) {
                return Integer.toString(chargingProfilePk);
            } else {
                return chargingProfilePk + " (" + description + ")";
            }
        }
    }

    @Getter
    @Builder
    public static final class Overview {
        private final int chargingProfilePk;
        private final int stackLevel;
        private final String description, profilePurpose, profileKind, recurrencyKind;
        private final Instant validFrom, validTo;
    }

    @Getter
    @Builder
    public static final class Details {
        // from ChargingProfileRecord
        private final int chargingProfilePk;
        private final int stackLevel;
        private final String chargingProfilePurpose;
        private final String chargingProfileKind;
        private final String recurrencyKind;
        private final Instant validFrom;
        private final Instant validTo;
        private final Integer durationInSeconds;
        private final Instant startSchedule;
        private final String chargingRateUnit;
        private final BigDecimal minChargingRate;
        // from ChargingSchedulePeriodRecord
        private final List<ChargingSchedulePeriod> periods;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class ChargingSchedulePeriod {
        private final int startPeriodInSeconds;
        private final BigDecimal powerLimit;
        private final Integer numberPhases;
    }
}

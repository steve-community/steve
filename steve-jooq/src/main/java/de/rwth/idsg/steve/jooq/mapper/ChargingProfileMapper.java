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
package de.rwth.idsg.steve.jooq.mapper;

import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import jooq.steve.db.tables.records.ChargingProfileRecord;
import jooq.steve.db.tables.records.ChargingSchedulePeriodRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargingProfileMapper {

    public static ChargingProfile.Details fromRecord(
            ChargingProfileRecord profile, List<ChargingSchedulePeriodRecord> periods) {
        return ChargingProfile.Details.builder()
                .chargingProfilePk(profile.getChargingProfilePk())
                .stackLevel(profile.getStackLevel())
                .chargingProfilePurpose(profile.getChargingProfilePurpose())
                .chargingProfileKind(profile.getChargingProfileKind())
                .recurrencyKind(profile.getRecurrencyKind())
                .validFrom(toInstant(profile.getValidFrom()))
                .validTo(toInstant(profile.getValidTo()))
                .durationInSeconds(profile.getDurationInSeconds())
                .startSchedule(toInstant(profile.getStartSchedule()))
                .chargingRateUnit(profile.getChargingRateUnit())
                .minChargingRate(profile.getMinChargingRate())
                .periods(periods.stream().map(ChargingProfileMapper::fromRecord).collect(Collectors.toList()))
                .build();
    }

    private static ChargingProfile.ChargingSchedulePeriod fromRecord(ChargingSchedulePeriodRecord p) {
        return new ChargingProfile.ChargingSchedulePeriod(
                p.getStartPeriodInSeconds(), p.getPowerLimit(), p.getNumberPhases());
    }
}

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
package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import jooq.steve.db.tables.records.ChargingProfileRecord;
import jooq.steve.db.tables.records.ChargingSchedulePeriodRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.RecurrencyKindType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargingProfileDetailsMapper {

    public static ChargingProfileForm mapToForm(ChargingProfile.Details details) {
        ChargingProfileRecord profile = details.getProfile();
        List<ChargingSchedulePeriodRecord> periods = details.getPeriods();

        ChargingProfileForm form = new ChargingProfileForm();
        form.setChargingProfilePk(profile.getChargingProfilePk());
        form.setDescription(profile.getDescription());
        form.setNote(profile.getNote());
        form.setStackLevel(profile.getStackLevel());
        form.setChargingProfilePurpose(ChargingProfilePurposeType.fromValue(profile.getChargingProfilePurpose()));
        form.setChargingProfileKind(ChargingProfileKindType.fromValue(profile.getChargingProfileKind()));
        form.setRecurrencyKind(profile.getRecurrencyKind() == null ? null : RecurrencyKindType.fromValue(profile.getRecurrencyKind()));
        form.setValidFrom(DateTimeUtils.toLocalDateTime(profile.getValidFrom()));
        form.setValidTo(DateTimeUtils.toLocalDateTime(profile.getValidTo()));
        form.setDurationInSeconds(profile.getDurationInSeconds());
        form.setStartSchedule(DateTimeUtils.toLocalDateTime(profile.getStartSchedule()));
        form.setChargingRateUnit(ChargingRateUnitType.fromValue(profile.getChargingRateUnit()));
        form.setMinChargingRate(profile.getMinChargingRate());

        Map<String, ChargingProfileForm.SchedulePeriod> periodMap = new LinkedHashMap<>();
        for (ChargingSchedulePeriodRecord rec : periods) {
            ChargingProfileForm.SchedulePeriod p = new ChargingProfileForm.SchedulePeriod();
            p.setStartPeriodInSeconds(rec.getStartPeriodInSeconds());
            p.setPowerLimit(rec.getPowerLimit());
            p.setNumberPhases(rec.getNumberPhases());

            periodMap.put(UUID.randomUUID().toString(), p);
        }
        form.setSchedulePeriodMap(periodMap);

        return form;
    }
}

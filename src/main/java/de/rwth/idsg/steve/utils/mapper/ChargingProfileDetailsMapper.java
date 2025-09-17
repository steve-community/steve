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
package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import jooq.steve.db.tables.records.ChargingProfileRecord;
import jooq.steve.db.tables.records.ChargingSchedulePeriodRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import ocpp.cp._2015._10.RecurrencyKindType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargingProfileDetailsMapper {

    public static ocpp.cp._2015._10.ChargingProfile mapToOcpp(ChargingProfile.Details details,
                                                              Integer transactionId) {
        ChargingProfileRecord profile = details.getProfile();

        List<ChargingSchedulePeriod> schedulePeriods =
            details.getPeriods()
                .stream()
                .map(k -> {
                    ChargingSchedulePeriod p = new ChargingSchedulePeriod();
                    p.setStartPeriod(k.getStartPeriodInSeconds());
                    p.setLimit(k.getPowerLimit());
                    p.setNumberPhases(k.getNumberPhases());
                    return p;
                })
                .collect(Collectors.toList());

        ChargingSchedule schedule = new ChargingSchedule()
            .withDuration(profile.getDurationInSeconds())
            .withStartSchedule(profile.getStartSchedule())
            .withChargingRateUnit(ChargingRateUnitType.fromValue(profile.getChargingRateUnit()))
            .withMinChargingRate(profile.getMinChargingRate())
            .withChargingSchedulePeriod(schedulePeriods);

        return new ocpp.cp._2015._10.ChargingProfile()
            .withChargingProfileId(profile.getChargingProfilePk())
            .withTransactionId(transactionId)
            .withStackLevel(profile.getStackLevel())
            .withChargingProfilePurpose(ChargingProfilePurposeType.fromValue(profile.getChargingProfilePurpose()))
            .withChargingProfileKind(ChargingProfileKindType.fromValue(profile.getChargingProfileKind()))
            .withRecurrencyKind(profile.getRecurrencyKind() == null ? null : RecurrencyKindType.fromValue(profile.getRecurrencyKind()))
            .withValidFrom(profile.getValidFrom())
            .withValidTo(profile.getValidTo())
            .withChargingSchedule(schedule);
    }

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
        form.setValidFrom(profile.getValidFrom());
        form.setValidTo(profile.getValidTo());
        form.setDurationInSeconds(profile.getDurationInSeconds());
        form.setStartSchedule(profile.getStartSchedule());
        form.setChargingRateUnit(ChargingRateUnitType.fromValue(profile.getChargingRateUnit()));
        form.setMinChargingRate(profile.getMinChargingRate());
        form.setSchedulePeriods(periods.stream().map(ChargingProfileDetailsMapper::mapToFormPeriod).toList());

        return form;
    }

    private static ChargingProfileForm.SchedulePeriod mapToFormPeriod(ChargingSchedulePeriodRecord rec) {
        ChargingProfileForm.SchedulePeriod p = new ChargingProfileForm.SchedulePeriod();
        p.setStartPeriodInSeconds(rec.getStartPeriodInSeconds());
        p.setPowerLimit(rec.getPowerLimit());
        p.setNumberPhases(rec.getNumberPhases());
        return p;
    }
}

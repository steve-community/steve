/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileAssignmentQueryForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static jooq.steve.db.tables.ChargingProfile.CHARGING_PROFILE;
import static jooq.steve.db.tables.ConnectorChargingProfile.CONNECTOR_CHARGING_PROFILE;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class ChargingProfileRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private ChargingProfileRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void setProfile() {
        Integer profilePk = repository.add(chargingProfileForm());
        assertNoDatabaseException(() -> repository.setProfile(profilePk, KNOWN_CHARGE_BOX_ID, 1));

        Integer count = dslContext.selectCount()
            .from(CONNECTOR_CHARGING_PROFILE)
            .where(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(profilePk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void clearProfileByPkAndChargeBox() {
        Integer profilePk = repository.add(chargingProfileForm());
        repository.setProfile(profilePk, KNOWN_CHARGE_BOX_ID, 1);

        assertNoDatabaseException(() -> repository.clearProfile(profilePk, KNOWN_CHARGE_BOX_ID));

        Integer count = dslContext.selectCount()
            .from(CONNECTOR_CHARGING_PROFILE)
            .where(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(profilePk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void clearProfileByFilter() {
        Integer profilePk = repository.add(chargingProfileForm());
        repository.setProfile(profilePk, KNOWN_CHARGE_BOX_ID, 1);

        assertNoDatabaseException(() -> repository.clearProfile(KNOWN_CHARGE_BOX_ID, 1, ChargingProfilePurposeType.TX_PROFILE, 0));

        Integer count = dslContext.selectCount()
            .from(CONNECTOR_CHARGING_PROFILE)
            .where(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(profilePk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void getAssignments() {
        Integer profilePk = repository.add(chargingProfileForm());
        repository.setProfile(profilePk, KNOWN_CHARGE_BOX_ID, 1);

        var rows = assertNoDatabaseException(() -> repository.getAssignments(new ChargingProfileAssignmentQueryForm()));
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    public void getBasicInfo() {
        repository.add(chargingProfileForm());
        var rows = assertNoDatabaseException(repository::getBasicInfo);
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    public void getOverview() {
        repository.add(chargingProfileForm());
        var rows = assertNoDatabaseException(() -> repository.getOverview(new ChargingProfileQueryForm()));
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    public void getDetails() {
        Integer profilePk = repository.add(chargingProfileForm());
        var details = assertNoDatabaseException(() -> repository.getDetails(profilePk));
        Assertions.assertNotNull(details);
        Assertions.assertEquals(profilePk, details.getProfile().getChargingProfilePk());
    }

    @Test
    public void exists() {
        Integer profilePk = repository.add(chargingProfileForm());
        boolean exists = assertNoDatabaseException(() -> repository.exists(profilePk));
        Assertions.assertTrue(exists);
    }

    @Test
    public void add() {
        Integer profilePk = assertNoDatabaseException(() -> repository.add(chargingProfileForm()));
        Assertions.assertNotNull(profilePk);
    }

    @Test
    public void update() {
        Integer profilePk = repository.add(chargingProfileForm());
        var form = chargingProfileForm();
        form.setChargingProfilePk(profilePk);
        form.setDescription("updated-profile");
        assertNoDatabaseException(() -> repository.update(form));

        String description = dslContext.select(CHARGING_PROFILE.DESCRIPTION)
            .from(CHARGING_PROFILE)
            .where(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(profilePk))
            .fetchOne(CHARGING_PROFILE.DESCRIPTION);
        Assertions.assertEquals("updated-profile", description);
    }

    @Test
    public void delete() {
        Integer profilePk = repository.add(chargingProfileForm());
        assertNoDatabaseException(() -> repository.delete(profilePk));

        Integer count = dslContext.selectCount()
            .from(CHARGING_PROFILE)
            .where(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(profilePk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    private static ChargingProfileForm chargingProfileForm() {
        var form = new ChargingProfileForm();
        form.setDescription("it");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ocpp.cp._2015._10.ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ocpp.cp._2015._10.ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);

        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.ONE);
        form.setSchedulePeriods(java.util.List.of(period));
        return form;
    }
}

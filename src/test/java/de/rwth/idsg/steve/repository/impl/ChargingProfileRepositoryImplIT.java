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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
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
        assertNoDatabaseException(() -> repository.setProfile(1, KNOWN_CHARGE_BOX_ID, 1));
    }

    @Test
    public void clearProfileByPkAndChargeBox() {
        assertNoDatabaseException(() -> repository.clearProfile(1, KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void clearProfileByFilter() {
        assertNoDatabaseException(() -> repository.clearProfile(KNOWN_CHARGE_BOX_ID, 1, ChargingProfilePurposeType.TX_PROFILE, 0));
    }

    @Test
    public void getAssignments() {
        assertNoDatabaseException(() -> repository.getAssignments(new ChargingProfileAssignmentQueryForm()));
    }

    @Test
    public void getBasicInfo() {
        assertNoDatabaseException(repository::getBasicInfo);
    }

    @Test
    public void getOverview() {
        assertNoDatabaseException(() -> repository.getOverview(new ChargingProfileQueryForm()));
    }

    @Test
    public void getDetails() {
        assertNoDatabaseException(() -> repository.getDetails(1));
    }

    @Test
    public void exists() {
        assertNoDatabaseException(() -> repository.exists(1));
    }

    @Test
    public void add() {
        assertNoDatabaseException(() -> repository.add(chargingProfileForm()));
    }

    @Test
    public void update() {
        var form = chargingProfileForm();
        form.setChargingProfilePk(1);
        assertNoDatabaseException(() -> repository.update(form));
    }

    @Test
    public void delete() {
        assertNoDatabaseException(() -> repository.delete(1));
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

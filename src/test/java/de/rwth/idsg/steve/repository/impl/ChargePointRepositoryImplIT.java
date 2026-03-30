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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
public class ChargePointRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private ChargePointRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getRegistration() {
        assertNoDatabaseException(() -> repository.getRegistration(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void updateCpoName() {
        assertNoDatabaseException(() -> repository.updateCpoName(KNOWN_CHARGE_BOX_ID, "cpo"));
    }

    @Test
    public void getChargePointSelect() {
        assertNoDatabaseException(() -> repository.getChargePointSelect(OcppProtocol.V_16_JSON, List.of(), List.of()));
    }

    @Test
    public void getChargeBoxIds() {
        assertNoDatabaseException(repository::getChargeBoxIds);
    }

    @Test
    public void getChargeBoxIdPkPair() {
        assertNoDatabaseException(() -> repository.getChargeBoxIdPkPair(List.of(KNOWN_CHARGE_BOX_ID)));
    }

    @Test
    public void getOverview() {
        assertNoDatabaseException(() -> repository.getOverview(new ChargePointQueryForm()));
    }

    @Test
    public void getDetails() {
        assertNoDatabaseException(() -> repository.getDetails(1));
    }

    @Test
    public void getChargePointConnectorStatus() {
        assertNoDatabaseException(() -> repository.getChargePointConnectorStatus(new ConnectorStatusForm()));
    }

    @Test
    public void getNonZeroConnectorIds() {
        assertNoDatabaseException(() -> repository.getNonZeroConnectorIds(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void addChargePointList() {
        assertNoDatabaseException(() -> repository.addChargePointList(List.of(uniqueId("cp"))));
    }

    @Test
    public void addChargePoint() {
        assertNoDatabaseException(() -> repository.addChargePoint(chargePointForm(uniqueId("cp"))));
    }

    @Test
    public void updateChargePoint() {
        var form = chargePointForm(KNOWN_CHARGE_BOX_ID);
        form.setChargeBoxPk(1);
        assertNoDatabaseException(() -> repository.updateChargePoint(form));
    }

    @Test
    public void deleteChargePoint() {
        assertNoDatabaseException(() -> repository.deleteChargePoint(1));
    }

    private static ChargePointForm chargePointForm(String chargeBoxId) {
        var form = new ChargePointForm();
        form.setChargeBoxId(chargeBoxId);
        form.setRegistrationStatus("Accepted");
        form.setInsertConnectorStatusAfterTransactionMsg(true);
        form.setAddress(new Address());
        return form;
    }
}

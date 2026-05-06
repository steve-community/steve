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
import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import ocpp.cs._2015._10.RegistrationStatus;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;

/**
 * Created with assistance from GPT-5.3-Codex
 */
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
        var registration = assertNoDatabaseException(() -> repository.getRegistration(KNOWN_CHARGE_BOX_ID));
        Assertions.assertTrue(registration.isPresent());
        Assertions.assertNotNull(registration.get().ocppConfiguration());
        Assertions.assertTrue(registration.get().ocppConfiguration().isObject());
        Assertions.assertNull(registration.get().cpoName());
    }

    @Test
    public void updateOcppConfigurationAfterChangePreservesExistingKeys() {
        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.OCPP_CONFIGURATION, JSON.json("{\"CpoName\":\"old\",\"vendor.foo\":\"bar\"}"))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .execute();

        assertNoDatabaseException(() ->
            repository.updateOcppConfigurationAfterChange(KNOWN_CHARGE_BOX_ID, "CpoName", "new")
        );

        var config = dslContext.select(CHARGE_BOX.OCPP_CONFIGURATION)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.OCPP_CONFIGURATION);
        var node = JsonObjectMapper.INSTANCE.getMapper().readTree(config.data());

        Assertions.assertEquals("new", node.get("CpoName").asString());
        Assertions.assertEquals("bar", node.get("vendor.foo").asString());
    }

    @Test
    public void updateBasicAuthPassword() {
        assertNoDatabaseException(() -> repository.updateBasicAuthPassword(KNOWN_CHARGE_BOX_ID, "encoded-password"));

        String storedPassword = dslContext.select(CHARGE_BOX.AUTH_PASSWORD)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.AUTH_PASSWORD);
        Assertions.assertEquals("encoded-password", storedPassword);
    }

    @Test
    public void updateSecurityProfile() {
        assertNoDatabaseException(() -> repository.updateSecurityProfile(KNOWN_CHARGE_BOX_ID, OcppSecurityProfile.Profile_2));

        Integer securityProfile = dslContext.select(CHARGE_BOX.SECURITY_PROFILE)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.SECURITY_PROFILE);
        Assertions.assertEquals(OcppSecurityProfile.Profile_2.getValue(), securityProfile);
    }

    @Test
    public void updateOcppConfiguration() {
        assertNoDatabaseException(() ->
            repository.updateOcppConfiguration(KNOWN_CHARGE_BOX_ID, "{\"CpoName\":\"cpo\",\"A\":\"B\"}")
        );

        var config = dslContext.select(CHARGE_BOX.OCPP_CONFIGURATION)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.OCPP_CONFIGURATION);
        var node = JsonObjectMapper.INSTANCE.getMapper().readTree(config.data());

        Assertions.assertEquals("cpo", node.get("CpoName").asString());
        Assertions.assertEquals("B", node.get("A").asString());
    }

    @Test
    public void getChargePointSelect() {
        var result = assertNoDatabaseException(() -> repository.getChargePointSelect(OcppProtocol.V_16_JSON, List.of(), List.of()));
        Assertions.assertNotNull(result);
    }

    @Test
    public void getChargeBoxIds() {
        var ids = assertNoDatabaseException(repository::getChargeBoxIds);
        Assertions.assertTrue(ids.contains(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void getChargeBoxIdPkPair() {
        var map = assertNoDatabaseException(() -> repository.getChargeBoxIdPkPair(List.of(KNOWN_CHARGE_BOX_ID)));
        Assertions.assertTrue(map.containsKey(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void getOverview() {
        var rows = assertNoDatabaseException(() -> repository.getOverview(new ChargePointQueryForm()));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getDetails() {
        Integer pk = dslContext.select(CHARGE_BOX.CHARGE_BOX_PK)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.CHARGE_BOX_PK);
        Assertions.assertNotNull(pk);

        var details = assertNoDatabaseException(() -> repository.getDetails(pk));
        Assertions.assertNotNull(details);
    }

    @Test
    public void getChargePointConnectorStatus() {
        var statuses = assertNoDatabaseException(() -> repository.getChargePointConnectorStatus(new ConnectorStatusForm()));
        Assertions.assertNotNull(statuses);
    }

    @Test
    public void getNonZeroConnectorIds() {
        var connectorIds = assertNoDatabaseException(() -> repository.getNonZeroConnectorIds(KNOWN_CHARGE_BOX_ID));
        Assertions.assertNotNull(connectorIds);
    }

    @Test
    public void addChargePointList() {
        String chargeBoxId = uniqueId("cp");
        assertNoDatabaseException(() -> repository.addChargePointList(List.of(chargeBoxId)));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void addChargePoint() {
        String chargeBoxId = uniqueId("cp");
        Integer chargeBoxPk = assertNoDatabaseException(() -> repository.addChargePoint(chargePointForm(chargeBoxId)));
        Assertions.assertNotNull(chargeBoxPk);
    }

    @Test
    public void updateChargePoint() {
        var form = chargePointForm(KNOWN_CHARGE_BOX_ID);
        Integer chargeBoxPk = dslContext.select(CHARGE_BOX.CHARGE_BOX_PK)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .fetchOne(CHARGE_BOX.CHARGE_BOX_PK);
        form.setChargeBoxPk(chargeBoxPk);
        form.setDescription("updated-description");
        assertNoDatabaseException(() -> repository.updateChargePoint(form));

        String description = dslContext.select(CHARGE_BOX.DESCRIPTION)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk))
            .fetchOne(CHARGE_BOX.DESCRIPTION);
        Assertions.assertEquals("updated-description", description);
    }

    @Test
    public void deleteChargePoint() {
        String chargeBoxId = uniqueId("cp");
        Integer chargeBoxPk = repository.addChargePoint(chargePointForm(chargeBoxId));
        assertNoDatabaseException(() -> repository.deleteChargePoint(chargeBoxPk));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void auditTimestamps() {
        String chargeBoxId = uniqueId("cp");
        Integer chargeBoxPk = dslContext.insertInto(CHARGE_BOX)
            .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
            .returning(CHARGE_BOX.CHARGE_BOX_PK)
            .fetchOne()
            .getChargeBoxPk();

        var before = dslContext.select(CHARGE_BOX.CREATED_AT, CHARGE_BOX.UPDATED_AT)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk))
            .fetchOne();
        assertAuditTimestampsAreSet(before.value1(), before.value2());

        waitForTimestampTick();

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.DESCRIPTION, "audit-update")
            .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk))
            .execute();

        var after = dslContext.select(CHARGE_BOX.CREATED_AT, CHARGE_BOX.UPDATED_AT)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk))
            .fetchOne();
        assertAuditTimestampsAfterUpdate(before.value1(), before.value2(), after.value1(), after.value2());
    }

    private static ChargePointForm chargePointForm(String chargeBoxId) {
        var form = new ChargePointForm();
        form.setChargeBoxId(chargeBoxId);
        form.setRegistrationStatus(RegistrationStatus.ACCEPTED);
        form.setInsertConnectorStatusAfterTransactionMsg(true);
        form.setAddress(new Address());
        return form;
    }
}

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
package de.rwth.idsg.steve.certification.ocpp16;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.test.context.ActiveProfiles;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * OCPP 1.6 JSON certification security testcase TC_087_CSMS.
 */
@Slf4j
@ActiveProfiles(profiles = {"test", "test-TC_087_CSMS"})
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class Ocpp16JsonCsmsCertification_TC_087_CSMS_IT extends AbstractOcpp16JsonCsms {

    private static final String SECURE_PATH = "wss://localhost:8444/steve/websocket/CentralSystemService/";

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private ServerProperties serverProperties;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        log.info("----- START: {} -----", testInfo.getDisplayName());

        dslContext.settings().setExecuteLogging(false);

        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void test_TC_087_CSMS_TLS_ClientSideCertificate_ValidCertificate() {
        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.AUTH_PASSWORD, (String) null)
            .set(CHARGE_BOX.SECURITY_PROFILE, 3)
            .set(CHARGE_BOX.CPO_NAME, CPO_NAME)
            .set(CHARGE_BOX.CHARGE_POINT_SERIAL_NUMBER, "SN-01-8043621")
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = new OcppJsonChargePoint(
            OcppVersion.V_16,
            REGISTERED_CHARGE_BOX_ID,
            SECURE_PATH,
            null,
            serverProperties.getSsl()
        ).start();

        expectGetConfCpoName(chargePoint);

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        chargePoint.close();
    }
}

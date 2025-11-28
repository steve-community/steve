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
package de.rwth.idsg.steve.issues;

import com.google.common.collect.Lists;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * https://github.com/steve-community/steve/issues/73
 * https://github.com/steve-community/steve/issues/219
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.07.2018
 */
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment =  WebEnvironment.DEFINED_PORT)
public class Issue73FixTest {

    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private DSLContext dslContext;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup() {
        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void test() {
        ocpp.cs._2015._10.CentralSystemService client = getForOcpp16(getPath(serverProperties));

        String chargeBox1 = __DatabasePreparer__.getRegisteredChargeBoxId();
        String chargeBox2 = __DatabasePreparer__.getRegisteredChargeBoxId2();

        sendBoot(client, Lists.newArrayList(chargeBox1, chargeBox2));

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox2, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox2, AuthorizationStatus.CONCURRENT_TX);
    }

    private static void sendBoot(CentralSystemService client, List<String> chargeBoxIdList) {
        for (String chargeBoxId : chargeBoxIdList) {
            BootNotificationResponse boot = client.bootNotification(
                    new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    chargeBoxId);
            Assertions.assertNotNull(boot);
            Assertions.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
        }
    }

    private static void sendAuth(CentralSystemService client, String chargeBoxId, AuthorizationStatus expected) {
        AuthorizeResponse auth = client.authorize(new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), chargeBoxId);
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(expected, auth.getIdTagInfo().getStatus());
    }

    private void sendStartTx(CentralSystemService client, String chargeBoxId, AuthorizationStatus expected) {
        StartTransactionResponse start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0),
                chargeBoxId
        );
        Assertions.assertNotNull(start);
        Assertions.assertEquals(expected, start.getIdTagInfo().getStatus());
        Assertions.assertTrue(start.getTransactionId() > 0);
        Assertions.assertTrue(databasePreparer.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction());
    }
}

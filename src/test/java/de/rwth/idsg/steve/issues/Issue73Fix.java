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
package de.rwth.idsg.steve.issues;

import com.google.common.collect.Lists;
import de.rwth.idsg.steve.Application;
import de.rwth.idsg.steve.ApplicationProfile;
import de.rwth.idsg.steve.SteveConfiguration;
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
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * https://github.com/RWTH-i5-IDSG/steve/issues/73
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.07.2018
 */
public class Issue73Fix {

    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();
    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        Assertions.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
        Assertions.assertTrue(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

        __DatabasePreparer__.prepare();

        Application app = new Application();
        try {
            app.start();
            test();
        } finally {
            try {
                app.stop();
            } finally {
                __DatabasePreparer__.cleanUp();
            }
        }
    }

    private static void test() {
        ocpp.cs._2015._10.CentralSystemService client = getForOcpp16(path);

        String chargeBox1 = getRandomString();
        String chargeBox2 = getRandomString();

        sendBoot(client, Lists.newArrayList(chargeBox1, chargeBox2));

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox1);

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox2, AuthorizationStatus.CONCURRENT_TX);
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

    private static void sendStartTx(CentralSystemService client, String chargeBoxId) {
        StartTransactionResponse start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0),
                chargeBoxId
        );
        Assertions.assertNotNull(start);
        Assertions.assertTrue(start.getTransactionId() > 0);
        Assertions.assertTrue(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction());
    }
}

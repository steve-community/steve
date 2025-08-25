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
import de.rwth.idsg.steve.Application;
import de.rwth.idsg.steve.ApplicationProfile;
import de.rwth.idsg.steve.SteveConfigurationReader;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;

import java.time.OffsetDateTime;
import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/steve-community/steve/issues/73
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.07.2018
 */
public class Issue73Fix {

    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    private static String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        assertThat(config.getProfile()).isEqualTo(ApplicationProfile.TEST);
        assertThat(config.getOcpp().isAutoRegisterUnknownStations()).isTrue();

        __DatabasePreparer__.prepare(config);

        path = getPath(config);

        var app = new Application(config);
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
        var client = getForOcpp16(path);

        var chargeBox1 = getRandomString();
        var chargeBox2 = getRandomString();

        sendBoot(client, Lists.newArrayList(chargeBox1, chargeBox2));

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox1);

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox2, AuthorizationStatus.CONCURRENT_TX);
    }

    private static void sendBoot(CentralSystemService client, List<String> chargeBoxIdList) {
        for (var chargeBoxId : chargeBoxIdList) {
            var boot = client.bootNotification(
                    new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    chargeBoxId);
            assertThat(boot).isNotNull();
            assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);
        }
    }

    private static void sendAuth(CentralSystemService client, String chargeBoxId, AuthorizationStatus expected) {
        var auth = client.authorize(new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), chargeBoxId);
        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(expected);
    }

    private static void sendStartTx(CentralSystemService client, String chargeBoxId) {
        var start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0),
                chargeBoxId
        );
        assertThat(start).isNotNull();
        assertThat(start.getTransactionId()).isGreaterThan(0);
        assertThat(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction()).isTrue();
    }
}

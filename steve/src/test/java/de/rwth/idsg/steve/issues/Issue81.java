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

import de.rwth.idsg.steve.SteveConfigurationReader;
import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;

import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.07.2018
 */
@RequiredArgsConstructor
public class Issue81 extends StressTest {

    private final String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        var path = getPath(config);
        new Issue81(path).attack();
    }

    protected void attackInternal() throws Exception {
        var runnable = new StressTester.Runnable() {

            private final ThreadLocal<CentralSystemService> client = new ThreadLocal<>();
            private final ThreadLocal<String> chargeBoxId = new ThreadLocal<>();
            private final ThreadLocal<StartTransactionRequest> txRequest = new ThreadLocal<>();
            private final ThreadLocal<Integer> txId = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                client.set(getForOcpp16(path));
                chargeBoxId.set(Helpers.getRandomString());

                var boot = getForOcpp16(path).bootNotification(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        chargeBoxId.get());
                assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);

                var req = new StartTransactionRequest()
                        .withConnectorId(ThreadLocalRandom.current().nextInt())
                        .withIdTag(Helpers.getRandomString())
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(ThreadLocalRandom.current().nextInt());
                txRequest.set(req);

                Integer t1 = sendStartTx(client.get(), txRequest.get(), chargeBoxId.get());
                txId.set(t1);
            }

            @Override
            public void toRepeat() {
                var t2 = sendStartTx(client.get(), txRequest.get(), chargeBoxId.get());
                assertThat(t2).isEqualTo(txId.get());
            }

            @Override
            public void afterRepeat() {

            }
        };

        var tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }

    private static Integer sendStartTx(CentralSystemService client, StartTransactionRequest req, String chargeBoxId) {
        var start = client.startTransaction(req, chargeBoxId);
        assertThat(start).isNotNull();
        return start.getTransactionId();
    }
}

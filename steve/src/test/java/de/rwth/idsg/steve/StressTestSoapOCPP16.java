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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.SteveConfigurationReader;
import de.rwth.idsg.steve.utils.StressTester;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;

import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getRandomStrings;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.04.2018
 */
@RequiredArgsConstructor
public class StressTestSoapOCPP16 extends StressTest {

    private final String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        var path = getPath(config);
        new StressTestSoapOCPP16(path).attack();
    }

    protected void attackInternal() throws Exception {
        final var idTags = getRandomStrings(ID_TAG_COUNT);
        final var chargeBoxIds = getRandomStrings(CHARGE_BOX_COUNT);

        var runnable = new StressTester.Runnable() {

            private final ThreadLocal<String> threadLocalChargeBoxId = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                var client = getForOcpp16(path);
                var localRandom = ThreadLocalRandom.current();

                threadLocalChargeBoxId.set(chargeBoxIds.get(localRandom.nextInt(chargeBoxIds.size())));

                var chargeBoxId = threadLocalChargeBoxId.get();

                // to insert threadLocalChargeBoxId into db
                var boot = client.bootNotification(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        chargeBoxId);
                assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);
            }

            @Override
            public void toRepeat() {
                var client = getForOcpp16(path);
                var localRandom = ThreadLocalRandom.current();

                var chargeBoxId = threadLocalChargeBoxId.get();

                var idTag = idTags.get(localRandom.nextInt(idTags.size()));
                var connectorId = localRandom.nextInt(1, CONNECTOR_COUNT_PER_CHARGE_BOX + 1);
                var transactionStart = localRandom.nextInt(0, Integer.MAX_VALUE);
                var transactionStop = localRandom.nextInt(transactionStart + 1, Integer.MAX_VALUE);

                var heartbeat = client.heartbeat(
                        new HeartbeatRequest(),
                        chargeBoxId
                );
                assertThat(heartbeat).isNotNull();

                for (var i = 0; i <= CONNECTOR_COUNT_PER_CHARGE_BOX; i++) {
                    var status = client.statusNotification(
                            new StatusNotificationRequest()
                                    .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                    .withStatus(ChargePointStatus.AVAILABLE)
                                    .withConnectorId(i)
                                    .withTimestamp(OffsetDateTime.now()),
                            chargeBoxId
                    );
                    assertThat(status).isNotNull();
                }

                var auth = client.authorize(
                        new AuthorizeRequest().withIdTag(idTag),
                        chargeBoxId
                );
                assertThat(auth.getIdTagInfo().getStatus()).isNotEqualTo(AuthorizationStatus.ACCEPTED);

                var start = client.startTransaction(
                        new StartTransactionRequest()
                                .withConnectorId(connectorId)
                                .withIdTag(idTag)
                                .withTimestamp(OffsetDateTime.now())
                                .withMeterStart(transactionStart),
                        chargeBoxId
                );
                assertThat(start).isNotNull();

                var statusStart = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.CHARGING)
                                .withConnectorId(connectorId)
                                .withTimestamp(OffsetDateTime.now()),
                        chargeBoxId
                );
                assertThat(statusStart).isNotNull();

                var meter = client.meterValues(
                        new MeterValuesRequest()
                                .withConnectorId(connectorId)
                                .withTransactionId(start.getTransactionId())
                                .withMeterValue(getMeterValues(transactionStart, transactionStop)),
                        chargeBoxId
                );
                assertThat(meter).isNotNull();

                var stop = client.stopTransaction(
                        new StopTransactionRequest()
                                .withTransactionId(start.getTransactionId())
                                .withTimestamp(OffsetDateTime.now())
                                .withIdTag(idTag)
                                .withMeterStop(transactionStop),
                        chargeBoxId
                );
                assertThat(stop).isNotNull();

                var statusStop = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.AVAILABLE)
                                .withConnectorId(connectorId)
                                .withTimestamp(OffsetDateTime.now()),
                        chargeBoxId
                );
                assertThat(statusStop).isNotNull();
            }

            @Override
            public void afterRepeat() {

            }
        };

        var tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }
}

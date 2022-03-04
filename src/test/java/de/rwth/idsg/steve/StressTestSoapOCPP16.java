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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.StressTester;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getRandomStrings;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.04.2018
 */
public class StressTestSoapOCPP16 extends StressTest {

    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        new StressTestSoapOCPP16().attack();
    }

    protected void attackInternal() throws Exception {
        final List<String> idTags = getRandomStrings(ID_TAG_COUNT);
        final List<String> chargeBoxIds = getRandomStrings(CHARGE_BOX_COUNT);

        StressTester.Runnable runnable = new StressTester.Runnable() {

            private final ThreadLocal<String> threadLocalChargeBoxId = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                CentralSystemService client = getForOcpp16(path);
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                threadLocalChargeBoxId.set(chargeBoxIds.get(localRandom.nextInt(chargeBoxIds.size())));

                String chargeBoxId = threadLocalChargeBoxId.get();

                // to insert threadLocalChargeBoxId into db
                BootNotificationResponse boot = client.bootNotification(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        chargeBoxId);
                Assertions.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
            }

            @Override
            public void toRepeat() {
                CentralSystemService client = getForOcpp16(path);
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                String chargeBoxId = threadLocalChargeBoxId.get();

                String idTag = idTags.get(localRandom.nextInt(idTags.size()));
                int connectorId = localRandom.nextInt(1, CONNECTOR_COUNT_PER_CHARGE_BOX + 1);
                int transactionStart = localRandom.nextInt(0, Integer.MAX_VALUE);
                int transactionStop = localRandom.nextInt(transactionStart + 1, Integer.MAX_VALUE);

                HeartbeatResponse heartbeat = client.heartbeat(
                        new HeartbeatRequest(),
                        chargeBoxId
                );
                Assertions.assertNotNull(heartbeat);

                for (int i = 0; i <= CONNECTOR_COUNT_PER_CHARGE_BOX; i++) {
                    StatusNotificationResponse status = client.statusNotification(
                            new StatusNotificationRequest()
                                    .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                    .withStatus(ChargePointStatus.AVAILABLE)
                                    .withConnectorId(i)
                                    .withTimestamp(DateTime.now()),
                            chargeBoxId
                    );
                    Assertions.assertNotNull(status);
                }

                AuthorizeResponse auth = client.authorize(
                        new AuthorizeRequest().withIdTag(idTag),
                        chargeBoxId
                );
                Assertions.assertNotEquals(AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());

                StartTransactionResponse start = client.startTransaction(
                        new StartTransactionRequest()
                                .withConnectorId(connectorId)
                                .withIdTag(idTag)
                                .withTimestamp(DateTime.now())
                                .withMeterStart(transactionStart),
                        chargeBoxId
                );
                Assertions.assertNotNull(start);

                StatusNotificationResponse statusStart = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.CHARGING)
                                .withConnectorId(connectorId)
                                .withTimestamp(DateTime.now()),
                        chargeBoxId
                );
                Assertions.assertNotNull(statusStart);

                MeterValuesResponse meter = client.meterValues(
                        new MeterValuesRequest()
                                .withConnectorId(connectorId)
                                .withTransactionId(start.getTransactionId())
                                .withMeterValue(getMeterValues(transactionStart, transactionStop)),
                        chargeBoxId
                );
                Assertions.assertNotNull(meter);

                StopTransactionResponse stop = client.stopTransaction(
                        new StopTransactionRequest()
                                .withTransactionId(start.getTransactionId())
                                .withTimestamp(DateTime.now())
                                .withIdTag(idTag)
                                .withMeterStop(transactionStop),
                        chargeBoxId
                );
                Assertions.assertNotNull(stop);

                StatusNotificationResponse statusStop = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.AVAILABLE)
                                .withConnectorId(connectorId)
                                .withTimestamp(DateTime.now()),
                        chargeBoxId
                );
                Assertions.assertNotNull(statusStop);
            }

            @Override
            public void afterRepeat() {

            }
        };

        StressTester tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }
}
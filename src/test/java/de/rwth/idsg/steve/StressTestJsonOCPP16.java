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

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.StressTester;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
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
import java.util.concurrent.atomic.AtomicInteger;

import static de.rwth.idsg.steve.utils.Helpers.getJsonPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getRandomStrings;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.05.2018
 */
public class StressTestJsonOCPP16 extends StressTest {

    private static final String PATH = getJsonPath();
    private static final OcppVersion VERSION = OcppVersion.V_16;

    public static void main(String[] args) throws Exception {
        new StressTestJsonOCPP16().attack();
    }

    protected void attackInternal() throws Exception {
        final List<String> idTags = getRandomStrings(ID_TAG_COUNT);
        final List<String> chargeBoxIds = getRandomStrings(CHARGE_BOX_COUNT);

        StressTester.Runnable runnable = new StressTester.Runnable() {

            private final ThreadLocal<OcppJsonChargePoint> threadLocalChargePoint = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                String chargeBoxId = chargeBoxIds.get(localRandom.nextInt(chargeBoxIds.size()));
                threadLocalChargePoint.set(new OcppJsonChargePoint(VERSION, chargeBoxId, PATH));

                OcppJsonChargePoint chargePoint = threadLocalChargePoint.get();
                chargePoint.start();

                chargePoint.prepare(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        BootNotificationResponse.class,
                        bootResponse ->  Assertions.assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
                        error -> Assertions.fail()
                );
            }

            @Override
            public void toRepeat() {
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                OcppJsonChargePoint chargePoint = threadLocalChargePoint.get();

                String idTag = idTags.get(localRandom.nextInt(idTags.size()));
                int connectorId = localRandom.nextInt(1, CONNECTOR_COUNT_PER_CHARGE_BOX + 1);
                int transactionStart = localRandom.nextInt(0, Integer.MAX_VALUE);
                int transactionStop = localRandom.nextInt(transactionStart + 1, Integer.MAX_VALUE);

                chargePoint.prepare(
                        new HeartbeatRequest(), HeartbeatResponse.class,
                        Assertions::assertNotNull,
                        error -> Assertions.fail()
                );

                for (int i = 0; i <= CONNECTOR_COUNT_PER_CHARGE_BOX; i++) {
                    chargePoint.prepare(
                            new StatusNotificationRequest()
                                    .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                    .withStatus(ChargePointStatus.AVAILABLE)
                                    .withConnectorId(i)
                                    .withTimestamp(DateTime.now()),
                            StatusNotificationResponse.class,
                            Assertions::assertNotNull,
                            error -> Assertions.fail()
                    );
                }

                chargePoint.prepare(
                        new AuthorizeRequest().withIdTag(idTag),
                        AuthorizeResponse.class,
                        response -> Assertions.assertNotEquals(AuthorizationStatus.ACCEPTED, response.getIdTagInfo().getStatus()),
                        error -> Assertions.fail()
                );

                final AtomicInteger transactionId = new AtomicInteger(-1);

                chargePoint.prepare(
                        new StartTransactionRequest()
                                .withConnectorId(connectorId)
                                .withIdTag(idTag)
                                .withTimestamp(DateTime.now())
                                .withMeterStart(transactionStart),
                        StartTransactionResponse.class,
                        response -> {
                            Assertions.assertNotNull(response);
                            transactionId.set(response.getTransactionId());
                        },
                        error -> Assertions.fail()
                );

                // wait for StartTransactionResponse to arrive, since we need the transactionId from now on
                chargePoint.process();

                chargePoint.prepare(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.CHARGING)
                                .withConnectorId(connectorId)
                                .withTimestamp(DateTime.now()),
                        StatusNotificationResponse.class,
                        Assertions::assertNotNull,
                        error -> Assertions.fail()
                );

                chargePoint.prepare(
                        new MeterValuesRequest()
                                .withConnectorId(connectorId)
                                .withTransactionId(transactionId.get())
                                .withMeterValue(getMeterValues(transactionStart, transactionStop)),
                        MeterValuesResponse.class,
                        Assertions::assertNotNull,
                        error -> Assertions.fail()
                );

                chargePoint.prepare(
                        new StopTransactionRequest()
                                .withTransactionId(transactionId.get())
                                .withTimestamp(DateTime.now())
                                .withIdTag(idTag)
                                .withMeterStop(transactionStop),
                        StopTransactionResponse.class,
                        Assertions::assertNotNull,
                        error -> Assertions.fail()
                );

                chargePoint.prepare(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.AVAILABLE)
                                .withConnectorId(connectorId)
                                .withTimestamp(DateTime.now()),
                        StatusNotificationResponse.class,
                        Assertions::assertNotNull,
                        error -> Assertions.fail()
                );

                chargePoint.process();
            }

            @Override
            public void afterRepeat() {
                threadLocalChargePoint.get().close();
            }
        };

        StressTester tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }
}

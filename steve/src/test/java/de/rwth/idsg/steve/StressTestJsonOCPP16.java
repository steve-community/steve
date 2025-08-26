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

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.SteveConfigurationReader;
import de.rwth.idsg.steve.utils.StressTester;
import lombok.RequiredArgsConstructor;
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

import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static de.rwth.idsg.steve.utils.Helpers.getJsonPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getRandomStrings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.05.2018
 */
@RequiredArgsConstructor
public class StressTestJsonOCPP16 extends StressTest {

    private static final OcppVersion VERSION = OcppVersion.V_16;

    private final String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        var path = getJsonPath(config);
        new StressTestJsonOCPP16(path).attack();
    }

    protected void attackInternal() throws Exception {
        final var idTags = getRandomStrings(ID_TAG_COUNT);
        final var chargeBoxIds = getRandomStrings(CHARGE_BOX_COUNT);

        var runnable = new StressTester.Runnable() {

            private final ThreadLocal<OcppJsonChargePoint> threadLocalChargePoint = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                var localRandom = ThreadLocalRandom.current();

                var chargeBoxId = chargeBoxIds.get(localRandom.nextInt(chargeBoxIds.size()));
                threadLocalChargePoint.set(new OcppJsonChargePoint(VERSION, chargeBoxId, path));

                var chargePoint = threadLocalChargePoint.get();
                chargePoint.start();

                chargePoint.prepare(
                        new BootNotificationRequest()
                                .withChargePointVendor(getRandomString())
                                .withChargePointModel(getRandomString()),
                        BootNotificationResponse.class,
                        bootResponse -> assertThat(bootResponse.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED),
                        error -> fail()
                );
            }

            @Override
            public void toRepeat() {
                var localRandom = ThreadLocalRandom.current();

                var chargePoint = threadLocalChargePoint.get();

                var idTag = idTags.get(localRandom.nextInt(idTags.size()));
                var connectorId = localRandom.nextInt(1, CONNECTOR_COUNT_PER_CHARGE_BOX + 1);
                var transactionStart = localRandom.nextInt(0, Integer.MAX_VALUE);
                var transactionStop = localRandom.nextInt(transactionStart + 1, Integer.MAX_VALUE);

                chargePoint.prepare(
                        new HeartbeatRequest(), HeartbeatResponse.class,
                        response -> assertThat(response).isNotNull(),
                        error -> fail()
                );

                for (var i = 0; i <= CONNECTOR_COUNT_PER_CHARGE_BOX; i++) {
                    chargePoint.prepare(
                            new StatusNotificationRequest()
                                    .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                    .withStatus(ChargePointStatus.AVAILABLE)
                                    .withConnectorId(i)
                                    .withTimestamp(OffsetDateTime.now()),
                            StatusNotificationResponse.class,
                            response -> assertThat(response).isNotNull(),
                            error -> fail()
                    );
                }

                chargePoint.prepare(
                        new AuthorizeRequest().withIdTag(idTag),
                        AuthorizeResponse.class,
                        response -> assertThat(response.getIdTagInfo().getStatus()).isNotEqualTo(AuthorizationStatus.ACCEPTED),
                        error -> fail()
                );

                final var transactionId = new AtomicInteger(-1);

                chargePoint.prepare(
                        new StartTransactionRequest()
                                .withConnectorId(connectorId)
                                .withIdTag(idTag)
                                .withTimestamp(OffsetDateTime.now())
                                .withMeterStart(transactionStart),
                        StartTransactionResponse.class,
                        response -> {
                            assertThat(response).isNotNull();
                            transactionId.set(response.getTransactionId());
                        },
                        error -> fail()
                );

                // wait for StartTransactionResponse to arrive, since we need the transactionId from now on
                chargePoint.process();

                chargePoint.prepare(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.CHARGING)
                                .withConnectorId(connectorId)
                                .withTimestamp(OffsetDateTime.now()),
                        StatusNotificationResponse.class,
                        response -> assertThat(response).isNotNull(),
                        error -> fail()
                );

                chargePoint.prepare(
                        new MeterValuesRequest()
                                .withConnectorId(connectorId)
                                .withTransactionId(transactionId.get())
                                .withMeterValue(getMeterValues(transactionStart, transactionStop)),
                        MeterValuesResponse.class,
                        response -> assertThat(response).isNotNull(),
                        error -> fail()
                );

                chargePoint.prepare(
                        new StopTransactionRequest()
                                .withTransactionId(transactionId.get())
                                .withTimestamp(OffsetDateTime.now())
                                .withIdTag(idTag)
                                .withMeterStop(transactionStop),
                        StopTransactionResponse.class,
                        response -> assertThat(response).isNotNull(),
                        error -> fail()
                );

                chargePoint.prepare(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(ChargePointStatus.AVAILABLE)
                                .withConnectorId(connectorId)
                                .withTimestamp(OffsetDateTime.now()),
                        StatusNotificationResponse.class,
                        response -> assertThat(response).isNotNull(),
                        error -> fail()
                );

                chargePoint.process();
            }

            @Override
            public void afterRepeat() {
                threadLocalChargePoint.get().close();
            }
        };

        var tester = new StressTester(THREAD_COUNT, REPEAT_COUNT_PER_THREAD);
        tester.test(runnable);
        tester.shutDown();
    }
}

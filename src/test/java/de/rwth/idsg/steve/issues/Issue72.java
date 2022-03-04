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

import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * https://github.com/RWTH-i5-IDSG/steve/issues/72
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.06.2018
 */
public class Issue72 extends StressTest {

    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        new Issue72().attack();
    }

    protected void attackInternal() throws Exception {
        String idTag = __DatabasePreparer__.getRegisteredOcppTag();
        String chargeBoxId = Helpers.getRandomString();

        DateTime startDateTime = DateTime.now();
        DateTime stopDateTime = startDateTime.plusHours(5);

        int connectorId = 2;

        int meterStart = 444;
        int meterStop = 99999;

        BootNotificationResponse boot = getForOcpp16(path).bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                chargeBoxId);
        Assertions.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

        StartTransactionResponse start = getForOcpp16(path).startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(connectorId)
                        .withIdTag(idTag)
                        .withTimestamp(startDateTime)
                        .withMeterStart(meterStart),
                chargeBoxId
        );
        Assertions.assertNotNull(start);

        int transactionId = start.getTransactionId();

        StressTester.Runnable runnable = new StressTester.Runnable() {

            private final ThreadLocal<CentralSystemService> threadLocalClient = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                threadLocalClient.set(getForOcpp16(path));
            }

            @Override
            public void toRepeat() {
                MeterValuesResponse mvr = threadLocalClient.get().meterValues(
                        new MeterValuesRequest()
                                .withConnectorId(connectorId)
                                .withTransactionId(transactionId)
                                .withMeterValue(
                                        new MeterValue()
                                                .withTimestamp(stopDateTime)
                                                .withSampledValue(
                                                        new SampledValue()
                                                                .withValue("555")
                                                                .withUnit(UnitOfMeasure.WH))),
                        chargeBoxId
                );
                Assertions.assertNotNull(mvr);

                StopTransactionResponse stop = threadLocalClient.get().stopTransaction(
                        new StopTransactionRequest()
                                .withTransactionId(transactionId)
                                .withTimestamp(stopDateTime)
                                .withIdTag(idTag)
                                .withMeterStop(meterStop),
                        chargeBoxId
                );
                Assertions.assertNotNull(stop);
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
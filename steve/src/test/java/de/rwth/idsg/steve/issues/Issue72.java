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

import de.rwth.idsg.steve.utils.SteveConfigurationReader;
import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.UnitOfMeasure;

import java.time.OffsetDateTime;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/steve-community/steve/issues/72
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.06.2018
 */
@RequiredArgsConstructor
public class Issue72 extends StressTest {

    private final String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        var path = getPath(config);
        new Issue72(path).attack();
    }

    protected void attackInternal() throws Exception {
        var idTag = __DatabasePreparer__.getRegisteredOcppTag();
        var chargeBoxId = Helpers.getRandomString();

        var startDateTime = OffsetDateTime.now();
        var stopDateTime = startDateTime.plusHours(5);

        var connectorId = 2;

        var meterStart = 444;
        var meterStop = 99999;

        var boot = getForOcpp16(path).bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                chargeBoxId);
        assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);

        var start = getForOcpp16(path).startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(connectorId)
                        .withIdTag(idTag)
                        .withTimestamp(startDateTime)
                        .withMeterStart(meterStart),
                chargeBoxId
        );
        assertThat(start).isNotNull();

        var transactionId = start.getTransactionId();

        var runnable = new StressTester.Runnable() {

            private final ThreadLocal<CentralSystemService> threadLocalClient = new ThreadLocal<>();

            @Override
            public void beforeRepeat() {
                threadLocalClient.set(getForOcpp16(path));
            }

            @Override
            public void toRepeat() {
                var mvr = threadLocalClient.get().meterValues(
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
                assertThat(mvr).isNotNull();

                var stop = threadLocalClient.get().stopTransaction(
                        new StopTransactionRequest()
                                .withTransactionId(transactionId)
                                .withTimestamp(stopDateTime)
                                .withIdTag(idTag)
                                .withMeterStop(meterStop),
                        chargeBoxId
                );
                assertThat(stop).isNotNull();
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

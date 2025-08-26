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

import com.google.common.net.MediaType;
import de.rwth.idsg.steve.utils.SteveConfigurationReader;
import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.time.OffsetDateTime;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getHttpPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/steve-community/steve/issues/72
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.06.2018
 */
@RequiredArgsConstructor
public class Issue72LowLevelSoap extends StressTest {

    private final String path;

    public static void main(String[] args) throws Exception {
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        var path = getHttpPath(config);
        new Issue72LowLevelSoap(path).attack();
    }

    protected void attackInternal() throws Exception {
        var idTag = __DatabasePreparer__.getRegisteredOcppTag();
        var chargeBoxId = Helpers.getRandomString();

        var startDateTime = OffsetDateTime.parse("2018-06-27T01:10:10Z");
        var stopDateTime = OffsetDateTime.parse("2018-06-27T04:10:10Z");

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

        var body = buildRequest(path, chargeBoxId, transactionId, idTag, stopDateTime, meterStop);
        var contentType = ContentType.create(MediaType.SOAP_XML_UTF_8.type(), MediaType.SOAP_XML_UTF_8.charset().orNull());

        var req = RequestBuilder.post(path)
                                           .addHeader("SOAPAction", "urn://Ocpp/Cs/2015/10/StopTransaction")
                                           .setEntity(new StringEntity(body, contentType))
                                           .build();

        var httpClient = HttpClients.createDefault();

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
                                                                .withMeasurand(Measurand.ENERGY_ACTIVE_IMPORT_REGISTER)
                                                                .withValue("555")
                                                                .withUnit(UnitOfMeasure.WH))),
                        chargeBoxId
                );
                assertThat(mvr).isNotNull();

                try {
                    httpClient.execute(req, httpResponse -> {
                        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                            throw new RuntimeException("Not OK");
                        }
                        return null;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterRepeat() {
            }
        };

        try {
            var tester = new StressTester(100, 100);
            tester.test(runnable);
            tester.shutDown();
        } finally {
            httpClient.close();
        }
    }

    private static String buildRequest(String path, String chargeBoxId, int transactionId, String idTag,
                                       OffsetDateTime stop, int meterStop) {
        return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Header><Action xmlns=\"http://www.w3.org/2005/08/addressing\">/StopTransaction</Action>" +
                "<MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">urn:uuid:47c9e1d9-a278-4e9c-8f08-565c29d86167</MessageID>" +
                "<To xmlns=\"http://www.w3.org/2005/08/addressing\">" + path + "</To>" +
                "<ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>http://www.w3.org/2005/08/addressing/anonymous</Address>" +
                "</ReplyTo><chargeBoxIdentity xmlns=\"urn://Ocpp/Cs/2015/10/\">" + chargeBoxId + "</chargeBoxIdentity>" +
                "</soap:Header>" +
                "<soap:Body><stopTransactionRequest xmlns=\"urn://Ocpp/Cs/2015/10/\"><transactionId>" + transactionId + "</transactionId>" +
                "<idTag>" + idTag + "</idTag>" +
                "<timestamp>" + stop + "</timestamp>" +
                "<meterStop>" + meterStop + "</meterStop>" +
                "</stopTransactionRequest></soap:Body></soap:Envelope>";
    }
}

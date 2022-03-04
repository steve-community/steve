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

import com.google.common.net.MediaType;
import de.rwth.idsg.steve.StressTest;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.StressTester;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
public class Issue72LowLevelSoap extends StressTest {

    private static final String path = getPath();

    public static void main(String[] args) throws Exception {
        new Issue72LowLevelSoap().attack();
    }

    protected void attackInternal() throws Exception {
        String idTag = __DatabasePreparer__.getRegisteredOcppTag();
        String chargeBoxId = Helpers.getRandomString();

        DateTime startDateTime = DateTime.parse("2018-06-27T01:10:10Z");
        DateTime stopDateTime = DateTime.parse("2018-06-27T04:10:10Z");

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

        String body = buildRequest(chargeBoxId, transactionId, idTag, stopDateTime, meterStop);
        ContentType contentType = ContentType.create(MediaType.SOAP_XML_UTF_8.type(), MediaType.SOAP_XML_UTF_8.charset().orNull());

        HttpUriRequest req = RequestBuilder.post(path)
                                           .addHeader("SOAPAction", "urn://Ocpp/Cs/2015/10/StopTransaction")
                                           .setEntity(new StringEntity(body, contentType))
                                           .build();

        CloseableHttpClient httpClient = HttpClients.createDefault();

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
                                                                .withMeasurand(Measurand.ENERGY_ACTIVE_IMPORT_REGISTER)
                                                                .withValue("555")
                                                                .withUnit(UnitOfMeasure.WH))),
                        chargeBoxId
                );
                Assertions.assertNotNull(mvr);

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
            StressTester tester = new StressTester(100, 100);
            tester.test(runnable);
            tester.shutDown();
        } finally {
            httpClient.close();
        }
    }

    private static String buildRequest(String chargeBoxId, int transactionId, String idTag,
                                       DateTime stop, int meterStop) {
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
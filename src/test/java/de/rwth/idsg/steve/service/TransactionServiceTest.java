/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.eichrecht.EichrechtService;
import de.rwth.idsg.steve.eichrecht.MeterPublicKeyService;
import de.rwth.idsg.steve.eichrecht.safe.ValuesXmlWriter;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.MeterPublicKeySources;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import ocpp.cs._2012._06.UnitOfMeasure;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class TransactionServiceTest {

    private static final int TRANSACTION_PK = 42;
    private static final String START_VALUE = "1000";
    private static final DateTime START_TIMESTAMP = DateTime.parse("2026-06-30T10:00:00Z");

    @Test
    public void findLast_usesPreviousStreakWhenLatestReadingBreaksMonotonicity() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);
        DateTime t4 = START_TIMESTAMP.plusMinutes(4);
        DateTime t5 = START_TIMESTAMP.plusMinutes(5);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1050", t1),
                meterValue("1100", t2),
                meterValue("1150", t3),
                meterValue("1200", t4),
                meterValue("1050", t5)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1200", last.getValue());
        Assertions.assertEquals(t4, last.getValueTimestamp());
    }

    @Test
    public void findLast_usesLatestReadingFromNewStreakAfterMonotonicityBreak() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);
        DateTime t4 = START_TIMESTAMP.plusMinutes(4);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1100", t1),
                meterValue("1200", t2),
                meterValue("900", t3),
                meterValue("1300", t4)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1300", last.getValue());
        Assertions.assertEquals(t4, last.getValueTimestamp());
    }

    @Test
    public void findLast_comparesKWhAndWhValuesWithinSameStreak() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1.1", UnitOfMeasure.K_WH.value(), t1),
                meterValue("1200", UnitOfMeasure.WH.value(), t2),
                meterValue("1.15", UnitOfMeasure.K_WH.value(), t3)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1200", last.getValue());
        Assertions.assertEquals(t2, last.getValueTimestamp());
    }

    @Test
    public void getSafeXml_beginAndEnd() throws Exception {
        var service = serviceWithSignedData(ocmf("T1", "B"), ocmf("T2", "E"));

        var signedData = service.getSignedData(transactionDetails());
        Assertions.assertTrue(signedData.hasBeginOrEnd());

        // The content of the XML is tested in ValuesXmlWriterTest
        Assertions.assertEquals(new ValuesXmlWriter().createXML(signedData), service.getSafeXml(TRANSACTION_PK));
    }

    @Test
    public void getSafeXml_queriesOnlySignedData() throws Exception {
        var repository = Mockito.mock(TransactionRepository.class);
        Mockito.when(repository.getMeterValues(TRANSACTION_PK, List.of("SignedData"))).thenReturn(List.of());
        var service = new TransactionService(new EichrechtService(), new MeterPublicKeyService(), new ValuesXmlWriter(),
            repository, Mockito.mock(OcppServerRepository.class));

        service.getSignedData(transactionDetails());

        Mockito.verify(repository).getMeterValues(TRANSACTION_PK, List.of("SignedData"));
    }

    @Test
    public void getSafeXml_noBeginOrEnd() throws Exception {
        for (var service : List.of(
            serviceWithSignedData(),
            serviceWithSignedData("<edl40/>"),
            serviceWithSignedData(ocmf("T1", "C"))
        )) {
            Assertions.assertFalse(service.getSignedData(transactionDetails()).hasBeginOrEnd());
            Assertions.assertThrows(SteveException.NotFound.class, () -> service.getSafeXml(TRANSACTION_PK));
        }
    }

    @Test
    public void getSignedData_publicKeyFromConfiguration() throws Exception {
        // Like the OCA application note: Base64 of "oca:base16:asn1:<key as printed on the meter>"
        String ocaValue = Base64.getEncoder()
            .encodeToString("oca:base16:asn1:3059 3013 0607".getBytes(StandardCharsets.US_ASCII));
        var sources = sources(1, "MeterPublicKey1", ocaValue);
        var service = serviceWithSignedData(sources, ocmf("T1", "B"), ocmf("T2", "E"));

        Assertions.assertEquals("305930130607", service.getSignedData(transactionDetails()).publicKey());
    }

    @Test
    public void getSignedData_publicKeyFromConfigurationForWholeStation() throws Exception {
        var sources = sources(1, "MeterPublicKey", "MFkwEwYHKoZIzj0CAQ==");
        var service = serviceWithSignedData(sources, ocmf("T1", "B"), ocmf("T2", "E"));

        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ==", service.getSignedData(transactionDetails()).publicKey());
    }

    @Test
    public void getSafeXml_transactionNotFound() throws Exception {
        var repository = Mockito.mock(TransactionRepository.class);
        Mockito.when(repository.getTransactions(Mockito.any())).thenReturn(List.of());
        var service = new TransactionService(new EichrechtService(), new MeterPublicKeyService(), new ValuesXmlWriter(),
            repository, Mockito.mock(OcppServerRepository.class));

        Assertions.assertThrows(SteveException.NotFound.class, () -> service.getSafeXml(TRANSACTION_PK));
    }

    @Test
    public void getSignedData_publicKeyUnknown() throws Exception {
        var service = serviceWithSignedData(ocmf("T1", "B"), ocmf("T2", "E"));

        Assertions.assertNull(service.getSignedData(transactionDetails()).publicKey());
    }

    private static MeterPublicKeySources sources(int connectorId, String configurationKey, String value) {
        var config = JsonObjectMapper.INSTANCE.getMapper().createObjectNode().put(configurationKey, value);
        return new MeterPublicKeySources(connectorId, config);
    }

    private static TransactionService serviceWithSignedData(String... values) throws Exception {
        return serviceWithSignedData(null, values);
    }

    private static TransactionService serviceWithSignedData(MeterPublicKeySources sources,
                                                            String... values) throws Exception {
        var meterValues = Arrays.stream(values)
            .map(v -> TransactionDetails.MeterValues.builder()
                .value(v)
                .valueTimestamp(START_TIMESTAMP)
                .format("SignedData")
                .build())
            .toList();

        var repository = Mockito.mock(TransactionRepository.class);
        Mockito.when(repository.getMeterValues(Mockito.eq(TRANSACTION_PK), Mockito.anyList())).thenReturn(meterValues);
        Mockito.when(repository.getMeterPublicKeySources(TRANSACTION_PK)).thenReturn(sources);
        Mockito.when(repository.getTransactions(Mockito.any())).thenReturn(List.of(transactionDetails()));

        return new TransactionService(new EichrechtService(), new MeterPublicKeyService(), new ValuesXmlWriter(),
            repository, Mockito.mock(OcppServerRepository.class));
    }

    private static String ocmf(String pagination, String transactionType) {
        return """
            OCMF|{"FV":"1.0","PG":"%s","MS":"SN1",\
            "RD":[{"TX":"%s","RV":1.5,"RU":"kWh","ST":"G"}]}|{"SD":"3045"}""".formatted(pagination, transactionType);
    }

    private static Transaction transactionDetails() {
        return Transaction.builder()
            .id(TRANSACTION_PK)
            .chargeBoxId("charge-box")
            .startValue(START_VALUE)
            .startTimestamp(START_TIMESTAMP)
            .build();
    }

    private static TransactionDetails.MeterValues meterValue(String value, DateTime timestamp) {
        return meterValue(value, UnitOfMeasure.WH.value(), timestamp);
    }

    private static TransactionDetails.MeterValues meterValue(String value, String unit, DateTime timestamp) {
        return TransactionDetails.MeterValues.builder()
            .value(value)
            .valueTimestamp(timestamp)
            .unit(unit)
            .build();
    }
}

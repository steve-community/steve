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
package de.rwth.idsg.steve.eichrecht.safe;

import de.rwth.idsg.steve.eichrecht.EichrechtService;
import de.rwth.idsg.steve.eichrecht.MeterPublicKeyService;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import jakarta.xml.bind.JAXBContext;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ValuesXmlWriterTest {

    private static final int TX_ID = 42;

    private static final String BEGIN = ocmf("T1", "{\"TX\":\"B\",\"RV\":2935.6,\"RU\":\"kWh\",\"ST\":\"G\"}");
    private static final String END = ocmf("T2", "{\"TX\":\"E\",\"RV\":2947.85,\"RU\":\"kWh\",\"ST\":\"G\"}");

    private final EichrechtService eichrechtService = new EichrechtService();
    private final ValuesXmlWriter writer = createWriter();

    @Test
    public void beginAndEnd() throws Exception {
        String xml = writer.createXML(tx(BEGIN, END));
        List<Values.Value> values = roundTrip(xml);

        Assertions.assertEquals(2, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
        assertValue(values.get(1), "Transaction.End", END, TX_ID);

        // S.A.F.E. rejects an empty publicKey element, so it must be left out if unknown
        Assertions.assertFalse(xml.contains("publicKey"));
        // S.A.F.E. cannot verify string formats with whitespace around the value
        Assertions.assertTrue(xml.contains(">" + BEGIN.replace("&", "&amp;") + "</signedData>"));
    }

    @Test
    public void unreadableAndOtherValuesAreSkipped() throws Exception {
        String periodic = ocmf("T3", "{\"TX\":\"T\",\"RV\":2940.0,\"RU\":\"kWh\"}");
        List<Values.Value> values = roundTrip(writer.createXML(tx("<edl40/>", "OCMF|{broken|{}", BEGIN, periodic, END)));

        Assertions.assertEquals(2, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
        assertValue(values.get(1), "Transaction.End", END, TX_ID);
    }

    @Test
    public void missingEnd() throws Exception {
        List<Values.Value> values = roundTrip(writer.createXML(tx(BEGIN)));

        Assertions.assertEquals(1, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, null);
    }

    @Test
    public void missingBegin() throws Exception {
        List<Values.Value> values = roundTrip(writer.createXML(tx(END)));

        Assertions.assertEquals(1, values.size());
        assertValue(values.get(0), "Transaction.End", END, null);
    }

    @Test
    public void noBeginAndNoEnd() throws Exception {
        Assertions.assertTrue(roundTrip(writer.createXML(tx("<edl40/>"))).isEmpty());
        Assertions.assertTrue(roundTrip(writer.createXML(tx())).isEmpty());
    }

    @Test
    public void sameRecordRepeated_firstBeginAndLastEnd() throws Exception {
        String laterEnd = ocmf("T9", "{\"TX\":\"E\",\"RV\":2950.0,\"RU\":\"kWh\"}");
        List<Values.Value> values = roundTrip(writer.createXML(tx(BEGIN, BEGIN, END, laterEnd, laterEnd)));

        Assertions.assertEquals(2, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
        assertValue(values.get(1), "Transaction.End", laterEnd, TX_ID);
    }

    @Test
    public void oneRecordWithBeginAndEnd_includedOnce() throws Exception {
        String both = ocmf("T1", "{\"TX\":\"B\",\"RV\":1.0,\"RU\":\"kWh\"},{\"TX\":\"E\",\"RV\":3.5,\"RU\":\"kWh\"}");
        List<Values.Value> values = roundTrip(writer.createXML(tx(both, both)));

        Assertions.assertEquals(1, values.size());
        assertValue(values.get(0), "Transaction.Begin", both, null);
    }

    @Test
    public void publicKeyFromJsonWrapper() throws Exception {
        String wrapper = "{\"signedMeterData\":\"" + base64(BEGIN) + "\",\"encodingMethod\":\"OCMF\","
            + "\"publicKey\":\"MFkwEwYHKoZIzj0CAQ==\"}";

        List<Values.Value> values = roundTrip(writer.createXML(tx(wrapper, END)));

        Assertions.assertEquals(2, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
        Assertions.assertEquals(Values.EncodingType.base64, values.get(0).getPublicKey().getEncoding());
        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ==", values.get(0).getPublicKey().getValue());
        // One meter per transaction, so the end gets the key that was sent with the begin
        Assertions.assertEquals(Values.EncodingType.base64, values.get(1).getPublicKey().getEncoding());
        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ==", values.get(1).getPublicKey().getValue());
    }

    @Test
    public void base64ValueIsWrittenAsPlainOcmf() throws Exception {
        List<Values.Value> values = roundTrip(writer.createXML(tx(base64(BEGIN))));

        Assertions.assertEquals(1, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, null);
    }

    @Test
    public void otherStopTypesAreEnd() throws Exception {
        for (String tx : List.of("L", "R", "A", "P", " E ")) {
            String end = ocmf("T2", "{\"TX\":\"" + tx + "\",\"RV\":2947.85,\"RU\":\"kWh\"}");
            List<Values.Value> values = roundTrip(writer.createXML(tx(BEGIN, end)));

            Assertions.assertEquals(2, values.size(), "TX: " + tx);
            assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
            assertValue(values.get(1), "Transaction.End", end, TX_ID);
        }
    }

    @Test
    public void onlyTransactionPagination() throws Exception {
        String fiscalBegin = ocmf("F1", "{\"TX\":\"B\",\"RV\":1.0,\"RU\":\"kWh\"}");
        String fiscalEnd = ocmf("F2", "{\"TX\":\"E\",\"RV\":9.0,\"RU\":\"kWh\"}");
        String noPagination = BEGIN.replace("\"PG\":\"T1\",", "");

        List<Values.Value> values = roundTrip(writer.createXML(tx(fiscalBegin, noPagination, BEGIN, END, fiscalEnd)));

        Assertions.assertEquals(2, values.size());
        assertValue(values.get(0), "Transaction.Begin", BEGIN, TX_ID);
        assertValue(values.get(1), "Transaction.End", END, TX_ID);

        Assertions.assertTrue(roundTrip(writer.createXML(tx(fiscalBegin, fiscalEnd))).isEmpty());
    }

    @Test
    public void singleValue_hasNoTransactionIdInXml() throws Exception {
        Assertions.assertFalse(writer.createXML(tx(BEGIN)).contains("transactionId"));
        Assertions.assertTrue(writer.createXML(tx(BEGIN, END)).contains("transactionId=\"" + TX_ID + "\""));
    }

    /**
     * @param transactionId only set if there is a separate begin and end value. Otherwise, S.A.F.E. would try to
     *                      verify a single value as a transaction, which fails.
     */
    private static void assertValue(Values.Value v, String context, String ocmf, Integer transactionId) {
        Assertions.assertEquals(context, v.getContext());
        Assertions.assertEquals(transactionId, v.getTransactionId());
        Assertions.assertEquals("OCMF", v.getSignedData().getFormat());
        Assertions.assertEquals(Values.EncodingType.plain, v.getSignedData().getEncoding());
        Assertions.assertEquals(ocmf, v.getSignedData().getValue());
    }

    private static List<Values.Value> roundTrip(String xml) throws Exception {
        var unmarshaller = JAXBContext.newInstance(Values.class).createUnmarshaller();
        Values values = (Values) unmarshaller.unmarshal(new StringReader(xml));
        return values.getValues() == null ? List.of() : values.getValues();
    }

    private Values tx(String... rawValues) {
        var meterValues = Arrays.stream(rawValues)
            .map(v -> TransactionDetails.MeterValues.builder()
                .valueTimestamp(DateTime.now())
                .value(v)
                .format("SignedData")
                .build())
            .toList();
        var signedValues = eichrechtService.parse(meterValues);
        return ValuesMapper.map(TX_ID, signedValues, new MeterPublicKeyService().find(signedValues, null));
    }

    private static String ocmf(String pagination, String readings) {
        return "OCMF|{\"FV\":\"1.0\",\"PG\":\"" + pagination + "\",\"MS\":\"SN1\",\"TT\":\"0,30 EUR & more\","
            + "\"RD\":[" + readings + "]}|{\"SA\":\"ECDSA-secp256r1-SHA256\",\"SD\":\"3045022100AB\"}";
    }

    private static String base64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static ValuesXmlWriter createWriter() {
        try {
            return new ValuesXmlWriter();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

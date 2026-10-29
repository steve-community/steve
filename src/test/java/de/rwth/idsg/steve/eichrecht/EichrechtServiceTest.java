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
package de.rwth.idsg.steve.eichrecht;

import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedDataParseResult;
import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static de.rwth.idsg.steve.eichrecht.OcmfParserTest.OCMF;
import static de.rwth.idsg.steve.eichrecht.OcmfParserTest.PAYLOAD;
import static de.rwth.idsg.steve.eichrecht.OcmfParserTest.SIGNATURE;

public class EichrechtServiceTest {

    private final EichrechtService service = new EichrechtService();

    @Test
    public void plainOcmf() {
        SignedDataParseResult r = service.parse(OCMF);

        Assertions.assertEquals(SignedDataSource.OCMF_PLAIN, r.source());
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
        Assertions.assertNull(r.publicKey());
    }

    @Test
    public void plainOcmf_surroundingWhitespace() {
        SignedDataParseResult r = service.parse("  " + OCMF + "\n");
        Assertions.assertEquals(SignedDataSource.OCMF_PLAIN, r.source());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void base64Ocmf() {
        SignedDataParseResult r = service.parse(base64(OCMF));

        Assertions.assertEquals(SignedDataSource.OCMF_BASE64, r.source());
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_base64SignedMeterData() {
        String json = "{\"signedMeterData\":\"" + base64(OCMF) + "\",\"signingMethod\":\"ECDSA-secp256r1-SHA256\","
            + "\"encodingMethod\":\"OCMF\",\"publicKey\":\"MFkwEwYHKoZIzj0CAQ\"}";

        SignedDataParseResult r = service.parse(json);

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
        Assertions.assertEquals("ECDSA-secp256r1-SHA256", r.signingMethod());
        Assertions.assertEquals("OCMF", r.encodingMethod());
        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ", r.publicKey());
    }

    @Test
    public void jsonWrapper_plainSignedMeterData() {
        SignedDataParseResult r = service.parse("{\"signedMeterData\":" + quote(OCMF) + ",\"encodingMethod\":\"OCMF\"}");

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        assertSameRecord(r.ocmfRecord());
        Assertions.assertNull(r.publicKey());
    }

    @Test
    public void jsonWrapper_base64OfWholeObject() {
        SignedDataParseResult r = service.parse(base64("{\"signedMeterData\":" + quote(OCMF) + "}"));

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_unknownFieldsIgnored() {
        SignedDataParseResult r = service.parse("{\"signedMeterData\":" + quote(OCMF) + ",\"customData\":{\"vendorId\":\"x\"}}");

        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_notOcmf() {
        String json = "{\"signedMeterData\":\"" + base64("<edl40>...</edl40>") + "\",\"encodingMethod\":\"EDL\"}";

        SignedDataParseResult r = service.parse(json);

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().contains("EDL"));
    }

    @Test
    public void jsonWrapper_missingSignedMeterData() {
        SignedDataParseResult r = service.parse("{\"publicKey\":\"abc\"}");

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertEquals("abc", r.publicKey());
        Assertions.assertNotNull(r.parseError());
    }

    @Test
    public void jsonWrapper_malformedOcmfInside() {
        SignedDataParseResult r = service.parse("{\"signedMeterData\":" + quote("OCMF|{broken|{}") + "}");

        Assertions.assertEquals(SignedDataSource.JSON_WRAPPER, r.source());
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void malformedOcmf() {
        SignedDataParseResult r = service.parse("OCMF|{\"FV\":\"1.0\"");

        Assertions.assertEquals(SignedDataSource.OCMF_PLAIN, r.source());
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void ocmf_fieldWithUnexpectedType() {
        SignedDataParseResult r = service.parse("OCMF|{\"FV\":{\"x\":1}}|{\"SD\":\"AB\"}");

        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void malformedJson() {
        SignedDataParseResult r = service.parse("{not json");

        Assertions.assertEquals(SignedDataSource.UNSUPPORTED, r.source());
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertNotNull(r.parseError());
    }

    @Test
    public void unsupported() {
        for (String v : new String[]{null, "", "   ", "1234.5", "<signedMeterValue/>", "[1,2]", "OCMFX|{}|{}"}) {
            SignedDataParseResult r = service.parse(v);
            Assertions.assertEquals(SignedDataSource.UNSUPPORTED, r.source(), "value: " + v);
            Assertions.assertNull(r.ocmfRecord(), "value: " + v);
            Assertions.assertNotNull(r.parseError(), "value: " + v);
        }
    }

    @Test
    public void list_onlySignedDataAndLinkedToMeterValue() {
        var raw = mv("1000", "Raw");
        var noFormat = mv("1000", null);
        var signed = mv(OCMF, "SignedData");
        var broken = mv("<edl40/>", "SignedData");

        List<SignedMeterValue> result = service.parse(List.of(raw, noFormat, signed, broken));

        Assertions.assertEquals(2, result.size());

        Assertions.assertSame(signed, result.get(0).meterValue());
        assertSameRecord(result.get(0).signedDataParseResult().ocmfRecord());

        Assertions.assertSame(broken, result.get(1).meterValue());
        Assertions.assertEquals(SignedDataSource.UNSUPPORTED, result.get(1).signedDataParseResult().source());
    }

    @Test
    public void list_empty() {
        Assertions.assertTrue(service.parse(List.<TransactionDetails.MeterValues>of()).isEmpty());
    }

    private static void assertSameRecord(OcmfRecord o) {
        Assertions.assertNotNull(o);
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals(SIGNATURE, o.rawSignature());
        Assertions.assertEquals("T12345", o.payloadSection().pagination());
    }

    private static TransactionDetails.MeterValues mv(String value, String format) {
        return TransactionDetails.MeterValues.builder()
            .valueTimestamp(DateTime.now())
            .value(value)
            .format(format)
            .measurand("Energy.Active.Import.Register")
            .build();
    }

    private static String base64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}

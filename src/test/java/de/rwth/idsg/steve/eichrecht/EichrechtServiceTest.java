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

import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static de.rwth.idsg.steve.eichrecht.OcmfParserTest.OCMF;
import static de.rwth.idsg.steve.eichrecht.OcmfParserTest.PAYLOAD;

public class EichrechtServiceTest {

    private final EichrechtService service = new EichrechtService();

    @Test
    public void plainOcmf() {
        SignedMeterValue r = parse(OCMF);
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
        Assertions.assertNull(r.publicKey());
    }

    @Test
    public void plainOcmf_surroundingWhitespace() {
        SignedMeterValue r = parse("  " + OCMF + "\n");
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void base64Ocmf() {
        SignedMeterValue r = parse(base64(OCMF));
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_base64SignedMeterData() {
        String json = """
            {"signedMeterData":"%s","signingMethod":"ECDSA-secp256r1-SHA256","encodingMethod":"OCMF","publicKey":"MFkwEwYHKoZIzj0CAQ"}"""
            .formatted(base64(OCMF));

        SignedMeterValue r = parse(json);
        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ", r.publicKey());
    }

    @Test
    public void jsonWrapper_plainSignedMeterData() {
        SignedMeterValue r = parse("""
            {"signedMeterData":%s,"encodingMethod":"OCMF"}"""
            .formatted(quote(OCMF)));

        assertSameRecord(r.ocmfRecord());
        Assertions.assertNull(r.publicKey());
    }

    @Test
    public void jsonWrapper_base64OfWholeObject() {
        SignedMeterValue r = parse(base64("""
            {"signedMeterData":%s}"""
            .formatted(quote(OCMF))));

        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_unknownFieldsIgnored() {
        SignedMeterValue r = parse("""
            {"signedMeterData":%s,"customData":{"vendorId":"x"}}"""
            .formatted(quote(OCMF)));

        Assertions.assertNull(r.parseError());
        assertSameRecord(r.ocmfRecord());
    }

    @Test
    public void jsonWrapper_notOcmf() {
        String json = """
            {"signedMeterData":"%s","encodingMethod":"EDL"}"""
            .formatted(base64("<edl40>...</edl40>"));

        SignedMeterValue r = parse(json);
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().contains("EDL"));
    }

    @Test
    public void jsonWrapper_missingSignedMeterData() {
        SignedMeterValue r = parse("""
            {"publicKey":"abc"}""");

        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertEquals("abc", r.publicKey());
        Assertions.assertNotNull(r.parseError());
    }

    @Test
    public void jsonWrapper_malformedOcmfInside() {
        SignedMeterValue r = parse("""
            {"signedMeterData":%s}"""
            .formatted(quote("OCMF|{broken|{}")));

        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void malformedOcmf() {
        SignedMeterValue r = parse("""
            OCMF|{"FV":"1.0\"""");

        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void ocmf_fieldWithUnexpectedType() {
        SignedMeterValue r = parse("""
            OCMF|{"RD":[{"RV":{"x":1}}]}|{"SD":"AB"}""");

        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertTrue(r.parseError().startsWith("Malformed OCMF record"));
    }

    @Test
    public void malformedJson() {
        SignedMeterValue r = parse("{not json");
        Assertions.assertNull(r.ocmfRecord());
        Assertions.assertNotNull(r.parseError());
    }

    @Test
    public void unsupported() {
        for (String v : new String[]{null, "", "   ", "1234.5", "<signedMeterValue/>", "[1,2]", "OCMFX|{}|{}"}) {
            SignedMeterValue r = parse(v);
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
        assertSameRecord(result.get(0).ocmfRecord());

        Assertions.assertSame(broken, result.get(1).meterValue());
        Assertions.assertNull(result.get(1).ocmfRecord());
        Assertions.assertNotNull(result.get(1).parseError());
    }

    @Test
    public void list_empty() {
        Assertions.assertTrue(service.parse(List.<TransactionDetails.MeterValues>of()).isEmpty());
    }

    private static void assertSameRecord(OcmfRecord o) {
        Assertions.assertNotNull(o);
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals("T12345", o.payload().pagination());
    }

    private SignedMeterValue parse(String value) {
        return service.parse(List.of(mv(value, "SignedData"))).getFirst();
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

    /**
     * @return the text as JSON string, with quotes
     */
    private static String quote(String s) {
        return JsonMapper.shared().writeValueAsString(s);
    }
}

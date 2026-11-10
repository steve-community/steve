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
package de.rwth.idsg.steve.service.eichrecht;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionMeterValue;
import de.rwth.idsg.steve.service.eichrecht.model.input.OcmfRecord;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedDataCheck;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedDataCheck.Type;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData;
import de.rwth.idsg.steve.service.eichrecht.model.SignedMeterValue;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static de.rwth.idsg.steve.service.eichrecht.OcmfParserTest.OCMF;
import static de.rwth.idsg.steve.service.eichrecht.OcmfParserTest.PAYLOAD;

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
    public void jsonWrapper_signingMethod() {
        var ocmfWithoutAlgorithm = """
            OCMF|%s|{"SD":"AB"}""".formatted(PAYLOAD);
        var brainpool = "ECDSA-brainpool256r1-SHA256";

        // Kept next to the OCMF record, which stays as received. The checker decides which one applies.
        var r = parse(wrapper(ocmfWithoutAlgorithm, brainpool));
        Assertions.assertNull(r.parseError());
        Assertions.assertEquals(brainpool, r.signingMethod());
        Assertions.assertNull(r.ocmfRecord().signature().algorithm());
        Assertions.assertEquals(ocmfWithoutAlgorithm, r.ocmfRecord().rawInput());

        r = parse(wrapper(OCMF, brainpool));
        Assertions.assertEquals(brainpool, r.signingMethod());
        Assertions.assertEquals("ECDSA-secp256r1-SHA256", r.ocmfRecord().signature().algorithm());
        Assertions.assertEquals(OCMF, r.ocmfRecord().rawInput());

        Assertions.assertNull(parse(wrapper(ocmfWithoutAlgorithm, null)).signingMethod());
        Assertions.assertNull(parse(ocmfWithoutAlgorithm).signingMethod());
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
    public void signedMeterData_onlySignedDataAndLinkedToMeterValue() {
        var raw = mv("1000", "Raw");
        var noFormat = mv("1000", null);
        var signed = mv(OCMF, "SignedData");
        var broken = TransactionMeterValue.builder()
            .valueTimestamp(DateTime.parse("2026-04-14T10:00:00Z"))
            .value("<edl40/>")
            .format("SignedData")
            .build();

        var result = signedMeterData(List.of(raw, noFormat, signed, broken));

        Assertions.assertEquals(2, result.receivedCount());
        Assertions.assertEquals(1, result.unreadable().size());
        Assertions.assertEquals(broken.getValueTimestamp(), result.unreadable().getFirst().valueTimestamp());
        Assertions.assertEquals("Not an OCMF record", result.unreadable().getFirst().error());
    }

    @Test
    public void signedMeterData_beginReading() {
        var ocmfWithoutAlgorithm = """
            OCMF|%s|{"SD":"AB"}""".formatted(PAYLOAD);
        var brainpool = "ECDSA-brainpool256r1-SHA256";

        // The record as it goes into the S.A.F.E. XML, and the signing method that the check applied
        var data = signedMeterData(List.of(mv(wrapper(ocmfWithoutAlgorithm, brainpool), "SignedData")));
        Assertions.assertTrue(data.safeXmlAvailable());
        Assertions.assertEquals(SignedMeterData.Format.OCMF, data.begin().format());
        Assertions.assertEquals(ocmfWithoutAlgorithm, data.begin().signedData());
        Assertions.assertEquals(new BigDecimal("2935.6"), data.begin().value());
        Assertions.assertEquals("kWh", data.begin().unit());
        Assertions.assertNotNull(data.begin().valueTimestamp());
        Assertions.assertEquals(brainpool, data.begin().signingMethod());
        Assertions.assertNull(data.end());

        data = signedMeterData(List.of(mv(ocmfWithoutAlgorithm, "SignedData")));
        Assertions.assertEquals("ECDSA-secp256r1-SHA256", data.begin().signingMethod());
    }

    @Test
    public void signedMeterData_oneRecordWithBothReadings() {
        var both = """
            OCMF|{"FV":"1.0","PG":"T1","RD":[{"TX":"B","RV":1.5,"RU":"kWh"},{"TX":"E","RV":2.5,"RU":"kWh"}]}|{"SD":"AB"}""";

        // Sent twice, still one record
        var data = signedMeterData(List.of(mv(both, "SignedData"), mv(both, "SignedData")));

        // Begin and end have the same record, each with its own reading
        Assertions.assertEquals(both, data.begin().signedData());
        Assertions.assertEquals(both, data.end().signedData());
        Assertions.assertEquals(new BigDecimal("1.5"), data.begin().value());
        Assertions.assertEquals(new BigDecimal("2.5"), data.end().value());

        // Its signature is checked once
        var types = data.checks().stream().map(SignedDataCheck::type).toList();
        Assertions.assertEquals(List.of(Type.BeginSignature, Type.BeginValue, Type.EndValue), types);
    }

    @Test
    public void signedMeterData_empty() {
        var result = signedMeterData(List.of());
        Assertions.assertEquals(0, result.receivedCount());
        Assertions.assertTrue(result.unreadable().isEmpty());
        Assertions.assertFalse(result.safeXmlAvailable());
    }

    private static void assertSameRecord(OcmfRecord o) {
        Assertions.assertNotNull(o);
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals("T12345", o.payload().pagination());
    }

    private static String wrapper(String ocmf, String signingMethod) {
        return """
            {"signedMeterData":"%s","signingMethod":%s,"encodingMethod":"OCMF"}"""
            .formatted(base64(ocmf), signingMethod == null ? "null" : quote(signingMethod));
    }

    private static SignedMeterValue parse(String value) {
        return EichrechtService.parse(mv(value, "SignedData"));
    }

    private SignedMeterData signedMeterData(List<TransactionMeterValue> values) {
        var transaction = Transaction.builder().id(1).build();
        return service.getSignedMeterData(transaction, null, values);
    }

    private static TransactionMeterValue mv(String value, String format) {
        return TransactionMeterValue.builder()
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

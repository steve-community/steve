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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OcmfParserTest {

    // Example record in the shape of the OCMF spec example
    static final String PAYLOAD = "{\"FV\":\"1.0\",\"GI\":\"ABL SBC-301\",\"GS\":\"808829900001\",\"GV\":\"1.4p3\","
        + "\"PG\":\"T12345\",\"MV\":\"Phoenix Contact\",\"MM\":\"EEM-350-D-MCB\",\"MS\":\"BQ27400330016\","
        + "\"MF\":\"1.0\",\"IS\":true,\"IL\":\"VERIFIED\",\"IF\":[\"RFID_PLAIN\",\"OCPP_RS_TLS\"],"
        + "\"IT\":\"ISO14443\",\"ID\":\"1F2D3A4F5506C7\",\"TT\":\"Tarif 1\",\"CF\":\"1.2\","
        + "\"LC\":{\"LN\":\"cable\",\"LI\":1,\"LR\":25,\"LU\":\"mOhm\"},\"CT\":\"EVSEID\",\"CI\":\"DE*ABC*E123\","
        + "\"RD\":[{\"TM\":\"2018-07-24T13:22:04,000+0200 S\",\"TX\":\"B\",\"RV\":2935.6,\"RI\":\"1-b:1.8.0\","
        + "\"RU\":\"kWh\",\"RT\":\"AC\",\"CL\":0.0,\"EF\":\"\",\"ST\":\"G\"}]}";
    static final String SIGNATURE = "{\"SA\":\"ECDSA-secp256r1-SHA256\",\"SE\":\"hex\",\"SM\":\"application/x-der\","
        + "\"SD\":\"887FABF407AC82782EEFFF2220C2F856AEB0BC22364BBCC6B55761911ED651D1A922BADA88818C9671AFEE7094D7F536\"}";
    static final String OCMF = "OCMF|" + PAYLOAD + "|" + SIGNATURE;

    private final OcmfParser parser = new OcmfParser();

    @Test
    public void allFields() {
        OcmfRecord o = parser.parse(OCMF);

        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals(SIGNATURE, o.rawSignature());

        var p = o.payloadSection();
        Assertions.assertEquals("1.0", p.formatVersion());
        Assertions.assertEquals("ABL SBC-301", p.gatewayId());
        Assertions.assertEquals("808829900001", p.gatewaySerial());
        Assertions.assertEquals("1.4p3", p.gatewayVersion());
        Assertions.assertEquals("T12345", p.pagination());
        Assertions.assertEquals("Phoenix Contact", p.meterVendor());
        Assertions.assertEquals("EEM-350-D-MCB", p.meterModel());
        Assertions.assertEquals("BQ27400330016", p.meterSerial());
        Assertions.assertEquals("1.0", p.meterFirmware());
        Assertions.assertEquals("true", p.identificationStatus());
        Assertions.assertEquals("VERIFIED", p.identificationLevel());
        Assertions.assertEquals(List.of("RFID_PLAIN", "OCPP_RS_TLS"), p.identificationFlags());
        Assertions.assertEquals("ISO14443", p.identificationType());
        Assertions.assertEquals("1F2D3A4F5506C7", p.identificationData());
        Assertions.assertEquals("Tarif 1", p.tariffText());
        Assertions.assertEquals("1.2", p.chargeControllerFirmwareVersion());
        Assertions.assertEquals("EVSEID", p.chargePointIdType());
        Assertions.assertEquals("DE*ABC*E123", p.chargePointId());

        var lc = p.lossCompensation();
        Assertions.assertEquals("cable", lc.naming());
        Assertions.assertEquals("1", lc.identification());
        Assertions.assertEquals("25", lc.cableResistance());
        Assertions.assertEquals("mOhm", lc.cableResistanceUnit());

        Assertions.assertEquals(1, p.readings().size());
        var r = p.readings().getFirst();
        Assertions.assertEquals("2018-07-24T13:22:04,000+0200 S", r.time());
        Assertions.assertEquals("B", r.transactionType());
        Assertions.assertEquals("2935.6", r.value());
        Assertions.assertEquals("1-b:1.8.0", r.identifier());
        Assertions.assertEquals("kWh", r.unit());
        Assertions.assertEquals("AC", r.currentType());
        Assertions.assertEquals("0.0", r.cumulatedLoss());
        Assertions.assertEquals("", r.errorFlags());
        Assertions.assertEquals("G", r.status());

        var s = o.signatureSection();
        Assertions.assertEquals("ECDSA-secp256r1-SHA256", s.signatureAlgorithm());
        Assertions.assertEquals("hex", s.signatureEncoding());
        Assertions.assertEquals("application/x-der", s.signatureMimeType());
        Assertions.assertTrue(s.signatureData().startsWith("887FAB"));
    }

    @Test
    public void minimalRecord_missingFieldsAreNullOrEmpty() {
        OcmfRecord o = parser.parse("OCMF|{}|{\"SD\":\"AB\"}");

        var p = o.payloadSection();
        Assertions.assertNull(p.formatVersion());
        Assertions.assertNull(p.lossCompensation());
        Assertions.assertTrue(p.identificationFlags().isEmpty());
        Assertions.assertTrue(p.readings().isEmpty());
        Assertions.assertNull(o.signatureSection().signatureAlgorithm());
        Assertions.assertEquals("AB", o.signatureSection().signatureData());
    }

    @Test
    public void surroundingWhitespace() {
        OcmfRecord o = parser.parse("  " + OCMF + "\n");
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals(SIGNATURE, o.rawSignature());
    }

    @Test
    public void readingValue_keptAsSent() {
        OcmfRecord o = parser.parse("OCMF|{\"RD\":[{\"TX\":\"E\",\"RV\":0.0300,\"RU\":\"kWh\"}]}|{\"SD\":\"AB\"}");
        Assertions.assertEquals("0.0300", o.payloadSection().readings().getFirst().value());
    }

    @Test
    public void pipeInsidePayloadValue() {
        String payload = "{\"TT\":\"0,30 EUR | kWh\",\"CI\":\"A|B\"}";
        OcmfRecord o = parser.parse("OCMF|" + payload + "|{\"SD\":\"AB\"}");

        Assertions.assertEquals(payload, o.rawPayload());
        Assertions.assertEquals("0,30 EUR | kWh", o.payloadSection().tariffText());
        Assertions.assertEquals("A|B", o.payloadSection().chargePointId());
    }

    @Test
    public void wrongPrefix() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse("OCMFX|{}|{}"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse("{}|{}"));
    }

    @Test
    public void missingSignatureSection() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse("OCMF|{}"));
    }

    @Test
    public void sectionNotAnObject() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse("OCMF|[]|{}"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse("OCMF|{}|\"x\""));
    }

    @Test
    public void malformedJson() {
        Assertions.assertThrows(RuntimeException.class, () -> parser.parse("OCMF|{broken|{}"));
    }
}

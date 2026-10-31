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

import java.math.BigDecimal;

public class OcmfParserTest {

    // Example record in the shape of the OCMF spec example
    static final String PAYLOAD = """
        {"FV":"1.0","GI":"ABL SBC-301","GS":"808829900001","GV":"1.4p3",\
        "PG":"T12345","MV":"Phoenix Contact","MM":"EEM-350-D-MCB","MS":"BQ27400330016",\
        "MF":"1.0","IS":true,"IL":"VERIFIED","IF":["RFID_PLAIN","OCPP_RS_TLS"],\
        "IT":"ISO14443","ID":"1F2D3A4F5506C7","TT":"Tarif 1","CF":"1.2",\
        "LC":{"LN":"cable","LI":1,"LR":25,"LU":"mOhm"},"CT":"EVSEID","CI":"DE*ABC*E123",\
        "RD":[{"TM":"2018-07-24T13:22:04,000+0200 S","TX":"B","RV":2935.6,"RI":"1-b:1.8.0",\
        "RU":"kWh","RT":"AC","CL":0.0,"EF":"","ST":"G"}]}""";

    static final String SIGNATURE = """
        {"SA":"ECDSA-secp256r1-SHA256","SE":"hex","SM":"application/x-der",\
        "SD":"887FABF407AC82782EEFFF2220C2F856AEB0BC22364BBCC6B55761911ED651D1A922BADA88818C9671AFEE7094D7F536"}""";

    static final String OCMF = "OCMF|" + PAYLOAD + "|" + SIGNATURE;

    @Test
    public void parse() {
        OcmfRecord o = OcmfParser.parse(OCMF);

        Assertions.assertEquals(OCMF, o.rawInput());
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
        Assertions.assertEquals("T12345", o.payload().pagination());
        Assertions.assertTrue(OcmfUtils.isTransactionRecord(o));

        Assertions.assertEquals(1, o.payload().readings().size());
        var r = OcmfUtils.beginReading(o);
        Assertions.assertEquals("B", r.transactionType());
        Assertions.assertEquals(new BigDecimal("2935.6"), r.value());
        Assertions.assertEquals("kWh", r.unit());
        Assertions.assertNull(OcmfUtils.endReading(o));

        Assertions.assertEquals("ECDSA-secp256r1-SHA256", o.signature().algorithm());
        Assertions.assertEquals("hex", o.signature().encoding());
        Assertions.assertTrue(o.signature().data().startsWith("887FAB"));
    }

    @Test
    public void minimalRecord() {
        OcmfRecord o = OcmfParser.parse("""
            OCMF|{}|{"SD":"AB"}""");

        Assertions.assertNull(o.payload().pagination());
        Assertions.assertFalse(OcmfUtils.isTransactionRecord(o));
        Assertions.assertNull(OcmfUtils.beginReading(o));
        Assertions.assertNull(OcmfUtils.endReading(o));
        Assertions.assertNull(o.signature().algorithm());
        Assertions.assertEquals("AB", o.signature().data());
    }

    @Test
    public void surroundingWhitespace() {
        OcmfRecord o = OcmfParser.parse("  " + OCMF + "\n");
        Assertions.assertEquals(OCMF, o.rawInput());
        Assertions.assertEquals(PAYLOAD, o.rawPayload());
    }

    @Test
    public void pipeInsidePayloadValue() {
        String payload = """
            {"TT":"0,30 EUR | kWh","PG":"T1"}""";

        OcmfRecord o = OcmfParser.parse("""
            OCMF|%s|{"SD":"AB"}"""
            .formatted(payload));

        Assertions.assertEquals(payload, o.rawPayload());
        Assertions.assertEquals("T1", o.payload().pagination());
    }

    @Test
    public void endReadings() {
        for (String type : new String[]{"E", "L", "R", "A", "P"}) {
            var o = OcmfParser.parse("""
                OCMF|{"RD":[{"TX":"%s"}]}|{}"""
                .formatted(type));

            Assertions.assertNotNull(OcmfUtils.endReading(o), type);
        }

        var other = OcmfParser.parse("""
            OCMF|{"RD":[{"TX":"T"}]}|{}""");

        Assertions.assertNull(OcmfUtils.endReading(other));
    }

    @Test
    public void valueInWh() {
        Assertions.assertEquals(0, new BigDecimal("2935600")
            .compareTo(OcmfUtils.valueInWh(new OcmfRecord.Reading("B", new BigDecimal("2935.6"), "kWh"))));

        var wh = new OcmfRecord.Reading("B", new BigDecimal("7753"), "Wh");
        Assertions.assertEquals(new BigDecimal("7753"), OcmfUtils.valueInWh(wh));
        Assertions.assertNull(OcmfUtils.valueInWh(new OcmfRecord.Reading("B", BigDecimal.ONE, "varh")));
        Assertions.assertNull(OcmfUtils.valueInWh(new OcmfRecord.Reading("B", null, "kWh")));
    }

    @Test
    public void invalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> OcmfParser.parse("OCMFX|{}|{}"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> OcmfParser.parse("OCMF|{}"));
        Assertions.assertThrows(RuntimeException.class, () -> OcmfParser.parse("OCMF|[]|{}"));
        Assertions.assertThrows(RuntimeException.class, () -> OcmfParser.parse("OCMF|{broken|{}"));
        Assertions.assertThrows(RuntimeException.class, () -> OcmfParser.parse("""
            OCMF|{"RD":[{"RV":{}}]}|{}"""));
    }
}

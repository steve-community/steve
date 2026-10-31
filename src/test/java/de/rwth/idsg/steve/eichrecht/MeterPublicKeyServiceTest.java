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

import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.repository.dto.MeterPublicKeySources;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MeterPublicKeyServiceTest {

    private static final String HEX_KEY = "3059301306072A8648CE3D020106082A8648CE3D03010703420004E5CE";
    private static final String OTHER_HEX_KEY = "3059301306072A8648CE3D020106082A8648CE3D03010703420004AAAA";
    private static final String OCMF = """
        OCMF|{"FV":"1.0","PG":"T1","RD":[{"TX":"B"}]}|{"SD":"3045"}""";

    private final MeterPublicKeyService service = new MeterPublicKeyService();

    /**
     * Example of the OCA application note "Signed Meter Values in OCPP" (v1.0, chapter 5.3)
     */
    @Test
    public void unwrap_ocaFormat() {
        String noteExample = """
            b2NhOmJhc2UxNjphc24xOjMwNTYzMDEwMDYwNzJhODY0OGNlM2QwMjAxMDYwNTJiODEwNDAwMGEwMzQy
            MDAwNDQ2MGEwMmJhMjc2NmQ5YzQ0ZjAyM2VjYzBlNGU1ODY0NGE4N2FkZDFhYWRkNjMxN2U1ZmU0ZGNjZG
            IyOWIxNjNhMDFkOGE2Mjk3Yzg0YmM1MzBmODY0MzFlOTJmOGQ0NmFiMzc4MzAyNDdjMDVjYmQ5MmZhYzI1M
            jkyOWU3ZjYx
            """;

        String printedOnMeter = "3056301006072a8648ce3d020106052b8104000a03420004460a02ba2766d9c44f023ecc0e4e58644a87"
            + "add1aadd6317e5fe4dccdb29b163a01d8a6297c84bc530f86431e92f8d46ab37830247c05cbd92fac252929e7f61";

        Assertions.assertEquals(printedOnMeter, MeterPublicKeyService.unwrap(noteExample));
        String base16 = base64("oca:base16:asn1:0x3059 3013 0607");
        Assertions.assertEquals("305930130607", MeterPublicKeyService.unwrap(base16));
        Assertions.assertEquals("MFkwEw==", MeterPublicKeyService.unwrap(base64("oca:base64:asn1:MFkw Ew==")));
    }

    @Test
    public void unwrap_keyItself() {
        Assertions.assertEquals("305930130607", MeterPublicKeyService.unwrap(" 3059 3013\n0607 "));
        Assertions.assertEquals("MFkwEwYHKoZIzj0CAQ==", MeterPublicKeyService.unwrap("MFkwEwYHKoZIzj0CAQ=="));
        Assertions.assertNull(MeterPublicKeyService.unwrap(null));
        Assertions.assertNull(MeterPublicKeyService.unwrap(" "));
    }

    @Test
    public void find_sentWithSignedValue() {
        var values = parse(OCMF, wrapper(OCMF, base64("oca:base16:asn1:" + HEX_KEY)));

        Assertions.assertEquals(HEX_KEY, service.find(values, sources("MeterPublicKey", OTHER_HEX_KEY)));
    }

    @Test
    public void find_fromConfiguration() {
        var values = parse(OCMF);
        var config = JsonObjectMapper.INSTANCE.getMapper().createObjectNode()
            .put("MeterPublicKey", OTHER_HEX_KEY)
            .put("MeterPublicKey2", HEX_KEY);

        Assertions.assertEquals(HEX_KEY, service.find(values, new MeterPublicKeySources(2, config)));
        // Another connector falls back to the key for all connectors
        Assertions.assertEquals(OTHER_HEX_KEY, service.find(values, new MeterPublicKeySources(1, config)));
    }

    @Test
    public void find_unknown() {
        var values = parse(OCMF);

        Assertions.assertNull(service.find(values, null));
        Assertions.assertNull(service.find(values, sources("OtherKey", HEX_KEY)));
        // Bender's default is an empty value
        Assertions.assertNull(service.find(values, sources("MeterPublicKey", "")));
    }

    private static MeterPublicKeySources sources(String configurationKey, String value) {
        var config = JsonObjectMapper.INSTANCE.getMapper().createObjectNode().put(configurationKey, value);
        return new MeterPublicKeySources(1, config);
    }

    private static List<SignedTransaction.SignedMeterValue> parse(String... rawValues) {
        var meterValues = Arrays.stream(rawValues)
            .map(v -> TransactionDetails.MeterValues.builder()
                .valueTimestamp(DateTime.now())
                .value(v)
                .format("SignedData")
                .build())
            .toList();
        return new EichrechtService().parse(meterValues);
    }

    private static String wrapper(String ocmf, String publicKey) {
        return """
            {"signedMeterData":"%s","encodingMethod":"OCMF","publicKey":"%s"}"""
            .formatted(base64(ocmf), publicKey);
    }

    private static String base64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.US_ASCII));
    }
}

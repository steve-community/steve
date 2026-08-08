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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import de.rwth.idsg.steve.web.dto.ocpp.InstallCertificateParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class CertificateUtilsTest {

    private static final String NORMALIZED_CERT =
        "-----BEGIN CERTIFICATE-----\nline-1\nline-2\n-----END CERTIFICATE-----";

    @ParameterizedTest
    @MethodSource("pemSeparators")
    void normalizesPemSeparatorsToLf(String separator) {
        String cert = "-----BEGIN CERTIFICATE-----"
            + separator + "line-1"
            + separator + "line-2"
            + separator + "-----END CERTIFICATE-----";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals(NORMALIZED_CERT, output);
    }

    private static Stream<Arguments> pemSeparators() {
        return Stream.of(
            Arguments.of("\r\n"),
            Arguments.of("\r"),
            Arguments.of("\n"),
            Arguments.of("\t")
        );
    }

    @Test
    void normalizesMixedLineEndingsToLf() {
        String cert = "-----BEGIN CERTIFICATE-----\r\nline-1\rline-2\nline-3\t-----END CERTIFICATE-----";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals(
            "-----BEGIN CERTIFICATE-----\nline-1\nline-2\nline-3\n-----END CERTIFICATE-----",
            output
        );
    }

    @Test
    void decodesUrlEncodedPemBeforeNormalizingLineEndings() {
        String cert = "-----BEGIN%20CERTIFICATE-----%0D%0Aline-1%0Dline-2%0A-----END%20CERTIFICATE-----";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals(NORMALIZED_CERT, output);
    }

    @Test
    void preservesRawPlusSignsWhenPercentDecodingLineEndings() {
        String cert = "-----BEGIN%20CERTIFICATE-----%0Aabc+def%0A-----END%20CERTIFICATE-----";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals("-----BEGIN CERTIFICATE-----\nabc+def\n-----END CERTIFICATE-----", output);
    }

    @Test
    void decodesPercentEncodedPlusSigns() {
        String cert = "-----BEGIN%20CERTIFICATE-----%0Aabc%2Bdef%0A-----END%20CERTIFICATE-----";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals("-----BEGIN CERTIFICATE-----\nabc+def\n-----END CERTIFICATE-----", output);
    }

    @Test
    void doesNotRewriteLiteralEscapeSequences() {
        String cert = "line-1\\r\\nline-2";

        String output = CertificateUtils.normalizePemInput(cert);

        Assertions.assertEquals("line-1\\r\\nline-2", output);
    }

    @Test
    void returnsNullForNullInput() {
        Assertions.assertNull(CertificateUtils.normalizePemInput(null));
    }

    @Test
    void isIdempotentAfterNormalization() {
        String cert = "-----BEGIN CERTIFICATE-----\r\nline-1\rline-2\t-----END CERTIFICATE-----";

        String once = CertificateUtils.normalizePemInput(cert);
        String twice = CertificateUtils.normalizePemInput(once);

        Assertions.assertEquals(once, twice);
    }

    @Test
    void installCertificateParamsNormalizeCertificate() {
        var params = new InstallCertificateParams();

        params.setCertificate("-----BEGIN CERTIFICATE-----\r\nline-1\rline-2\t-----END CERTIFICATE-----");

        Assertions.assertEquals(NORMALIZED_CERT, params.getCertificate());
    }

    @Test
    void signedUpdateFirmwareParamsNormalizeSigningCertificate() {
        var params = new SignedUpdateFirmwareParams();

        params.setSigningCertificate("-----BEGIN CERTIFICATE-----\r\nline-1\rline-2\t-----END CERTIFICATE-----");

        Assertions.assertEquals(NORMALIZED_CERT, params.getSigningCertificate());
    }

    @Test
    void certificateSignedParamsNormalizeCertificateChain() {
        var params = new CertificateSignedParams();

        params.setCertificateChain("-----BEGIN CERTIFICATE-----\r\nline-1\rline-2\t-----END CERTIFICATE-----");

        Assertions.assertEquals(NORMALIZED_CERT, params.getCertificateChain());
    }
}

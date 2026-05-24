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

import jooq.steve.db.enums.CertificateSignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import static de.rwth.idsg.steve.utils.CertificateUtils.isECDSAFamily;
import static de.rwth.idsg.steve.utils.CertificateUtils.isRSAFamily;
import static de.rwth.idsg.steve.utils.CertificateUtils.resolveSignatureAlgorithm;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.05.2026
 */
public record CertificateIssuerMaterial(
    CertificateSignatureAlgorithm name,
    X509Certificate caCertificate,
    PrivateKey caPrivateKey,
    List<X509Certificate> issuerCertificateChain,
    String certificateSignatureAlgorithm
) {

    public void validateFamily() {
        switch (name) {
            case RSA -> {
                if (!isRSAFamily(caCertificate.getPublicKey(), caPrivateKey)) {
                    throw new IllegalArgumentException("Configured '" + name + "' entry does not contain '" + name + "' CA certificate and/or private-key");
                }
            }
            case ECDSA -> {
                if (!isECDSAFamily(caCertificate.getPublicKey(), caPrivateKey)) {
                    throw new IllegalArgumentException("Configured '" + name + "' entry does not contain '" + name + "' CA certificate and/or private-key");
                }
            }
        }
    }

    public void validateCaCertificate() throws Exception {
        if (caCertificate.getBasicConstraints() < 0) {
            throw new IllegalArgumentException("Configured CA certificate for issuer '" + name + "' is not a CA certificate (basicConstraints CA=true required)");
        }

        boolean[] keyUsage = caCertificate.getKeyUsage();
        if (keyUsage == null || keyUsage.length <= 5 || !keyUsage[5]) {
            throw new IllegalArgumentException("Configured CA certificate for issuer '" + name + "' must allow keyCertSign in keyUsage");
        }

        String checkAlgorithm = resolveSignatureAlgorithm(caPrivateKey);
        byte[] dummyProbeData = "certificate-key-pair-check".getBytes(StandardCharsets.UTF_8);

        Signature signer = Signature.getInstance(checkAlgorithm, PROVIDER_NAME);
        signer.initSign(caPrivateKey);
        signer.update(dummyProbeData);
        byte[] signature = signer.sign();

        Signature verifier = Signature.getInstance(checkAlgorithm, PROVIDER_NAME);
        verifier.initVerify(caCertificate.getPublicKey());
        verifier.update(dummyProbeData);

        if (!verifier.verify(signature)) {
            throw new IllegalArgumentException("Configured CA private key does not match configured CA certificate public key for issuer '" + name + "'");
        }
    }

    public void validateCertificateChain() throws Exception {
        if (issuerCertificateChain.isEmpty()) {
            throw new IllegalArgumentException("Configured issuer certificate chain for issuer '" + name + "' is empty");
        }

        if (!Arrays.equals(issuerCertificateChain.getFirst().getEncoded(), caCertificate.getEncoded())) {
            throw new IllegalArgumentException("Configured issuer certificate chain for issuer '" + name + "' must start with the configured CA certificate");
        }

        for (int i = 0; i < issuerCertificateChain.size() - 1; i++) {
            var child = issuerCertificateChain.get(i);
            var issuer = issuerCertificateChain.get(i + 1);

            String messagePrefix = "Configured issuer certificate chain for issuer '" + name + "' contains non-CA issuer at position " + (i + 1);

            if (issuer.getBasicConstraints() < 0) {
                throw new IllegalArgumentException(messagePrefix + " (basicConstraints CA=true required)");
            }

            boolean[] keyUsage = issuer.getKeyUsage();
            if (keyUsage != null && (keyUsage.length <= 5 || !keyUsage[5])) {
                throw new IllegalArgumentException(messagePrefix + " (keyCertSign required when keyUsage is present)");
            }

            if (!child.getIssuerX500Principal().equals(issuer.getSubjectX500Principal())) {
                throw new IllegalArgumentException("Configured issuer certificate chain for issuer '" + name + "' is not ordered correctly at position " + i);
            }

            try {
                child.verify(issuer.getPublicKey());
            } catch (Exception e) {
                throw new IllegalArgumentException("Configured issuer certificate chain for issuer '" + name + "' has invalid signature link at position " + i, e);
            }
        }
    }
}

/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificateUtils {

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    public static X509Certificate parseCertificate(String certPem) throws Exception {
        // Nginx sends URL-encoded PEM, decode it
        String decoded = URLDecoder.decode(certPem, StandardCharsets.UTF_8);

        // Remove URL encoding artifacts and normalize
        decoded = decoded.replace("\t", "\n");

        // Ensure proper PEM format
        if (!decoded.contains(BEGIN_CERTIFICATE)) {
            decoded = BEGIN_CERTIFICATE + "\n" + decoded + "\n" + END_CERTIFICATE;
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded.getBytes(StandardCharsets.UTF_8))) {
            return (X509Certificate) cf.generateCertificate(bais);
        }
    }

    public static PKCS10CertificationRequest parseCsr(String csrPem) throws Exception {
        try (var reader = new StringReader(csrPem);
             var pemParser = new PEMParser(reader)) {
            var parsedObj = pemParser.readObject();

            if (parsedObj instanceof PKCS10CertificationRequest request) {
                return request;
            } else {
                throw new IllegalArgumentException("Invalid CSR format. Expected PKCS10 certificate request.");
            }
        }
    }

    public static String certificatesToPEM(X509Certificate... chain) throws Exception {
        var sw = new StringWriter();
        try (var pemWriter = new JcaPEMWriter(sw)) {
            for (var cert : chain) {
                pemWriter.writeObject(cert);
            }
        }
        return sw.toString();
    }

    public static String getCommonName(X509Certificate cert) {
        var x500name = new X500Name(cert.getSubjectX500Principal().getName());
        return getCommonName(x500name);
    }

    public static String getCommonName(X500Name x500name) {
        var rdns = x500name.getRDNs(BCStyle.CN);
        if (rdns.length > 0) {
            return rdns[0].getFirst().getValue().toString();
        }
        return null;
    }

    public static String getOrganization(X509Certificate cert) {
        var x500name = new X500Name(cert.getSubjectX500Principal().getName());
        return getOrganization(x500name);
    }

    public static String getOrganization(X500Name x500name) {
        var rdns = x500name.getRDNs(BCStyle.O);
        if (rdns.length > 0) {
            return rdns[0].getFirst().getValue().toString();
        }
        return null;
    }

    public static int getKeySize(X509Certificate cert) {
        var publicKey = cert.getPublicKey();

        if (publicKey instanceof RSAPublicKey rsaKey) {
            return rsaKey.getModulus().bitLength();

        } else if (publicKey instanceof ECPublicKey ecKey) {
            ECParameterSpec params = ecKey.getParams();
            return params.getOrder().bitLength();

        } else if (publicKey instanceof DSAPublicKey dsaKey) {
            return dsaKey.getParams().getP().bitLength();
        }

        log.error("Unknown public key type: {}", publicKey);
        return -1;
    }

    public static CertificateSignatureAlgorithm getSignatureAlgorithm(X509Certificate cert) {
        try {
            var algorithm = Certificate.getInstance(cert.getEncoded())
                .getSignatureAlgorithm()
                .getAlgorithm();

            if (algorithm.on(PKCSObjectIdentifiers.pkcs_1)) {
                // RSA signature algorithms (under pkcs-1 branch)
                return CertificateSignatureAlgorithm.RSA;

            } else if (algorithm.on(X9ObjectIdentifiers.id_ecSigType)
                || algorithm.equals(X9ObjectIdentifiers.ecdsa_with_SHA256)
                || algorithm.equals(X9ObjectIdentifiers.ecdsa_with_SHA384)
                || algorithm.equals(X9ObjectIdentifiers.ecdsa_with_SHA512)) {
                // ECDSA signature algorithms
                return CertificateSignatureAlgorithm.ECDSA;
            }

            throw new IllegalArgumentException("Unknown signature algorithm: " + algorithm);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

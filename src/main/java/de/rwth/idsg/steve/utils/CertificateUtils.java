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

import de.rwth.idsg.steve.config.SteveProperties.Ocpp.Security.CsrSigning.LocalCsrSigning.SignatureAlgorithmPolicy;
import jooq.steve.db.enums.CertificateSignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificateUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    public static X509Certificate parseCertificate(String certPem) throws Exception {
        String decoded = normalizePemInput(certPem);

        // Ensure proper PEM format
        if (!decoded.contains(BEGIN_CERTIFICATE)) {
            decoded = BEGIN_CERTIFICATE + "\n" + decoded + "\n" + END_CERTIFICATE;
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded.getBytes(StandardCharsets.UTF_8))) {
            return (X509Certificate) cf.generateCertificate(bais);
        }
    }

    public static List<X509Certificate> parseCertificates(String certPemBundle) throws Exception {
        String decoded = normalizePemInput(certPemBundle);
        if (!decoded.contains(BEGIN_CERTIFICATE)) {
            return List.of(parseCertificate(decoded));
        }

        var cf = CertificateFactory.getInstance("X.509");
        List<X509Certificate> certificates = new ArrayList<>();
        try (var bais = new ByteArrayInputStream(decoded.getBytes(StandardCharsets.UTF_8))) {
            for (var cert : cf.generateCertificates(bais)) {
                if (!(cert instanceof X509Certificate x509Cert)) {
                    throw new IllegalArgumentException("Configured certificate bundle contains non-X509 certificate");
                }
                certificates.add(x509Cert);
            }
        }

        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("Configured certificate bundle does not contain any certificate");
        }
        return certificates;
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

    public static PrivateKey parsePrivateKeyViaBouncyCastle(String privateKeyPem) throws Exception {
        try (var reader = new StringReader(privateKeyPem);
             var pemParser = new PEMParser(reader)) {
            var parsedObj = pemParser.readObject();

            if (parsedObj == null) {
                throw new IllegalArgumentException("Private key PEM is empty");
            }

            var converter = new JcaPEMKeyConverter().setProvider(PROVIDER_NAME);
            if (parsedObj instanceof PrivateKeyInfo privateKeyInfo) {
                return converter.getPrivateKey(privateKeyInfo);
            }
            if (parsedObj instanceof PEMKeyPair keyPair) {
                return converter.getKeyPair(keyPair).getPrivate();
            }

            throw new IllegalArgumentException("Unsupported private key PEM format");
        }
    }

    public static String resolveSignatureAlgorithm(PrivateKey privateKey,
                                                   SignatureAlgorithmPolicy signatureAlgorithmPolicy) {
        var policy = (signatureAlgorithmPolicy == null)
            ? SignatureAlgorithmPolicy.AUTO
            : signatureAlgorithmPolicy;

        String keyAlgorithm = privateKey.getAlgorithm();
        if ("RSA".equalsIgnoreCase(keyAlgorithm)) {
            return switch (policy) {
                case AUTO, RSA_PSS -> "SHA256withRSAandMGF1";
                case RSA_PKCS1 -> "SHA256WithRSA";
            };
        }
        if ("EC".equalsIgnoreCase(keyAlgorithm) || "ECDSA".equalsIgnoreCase(keyAlgorithm)) {
            return "SHA256withECDSA";
        }
        if ("DSA".equalsIgnoreCase(keyAlgorithm)) {
            return "SHA256withDSA";
        }
        throw new IllegalArgumentException("Unsupported signing private key algorithm: " + keyAlgorithm);
    }

    public static String certificatesToPEM(List<X509Certificate> chain) throws Exception {
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

    private static String normalizePemInput(String pem) {
        String normalized = pem;
        // Nginx/proxy headers can be URL-encoded; PEM files typically are not.
        if (pem.contains("%")) {
            normalized = URLDecoder.decode(pem, StandardCharsets.UTF_8);
        }
        return normalized.replace("\t", "\n");
    }
}

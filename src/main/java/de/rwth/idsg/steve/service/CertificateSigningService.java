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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.config.SecurityProfileConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateSigningService {

    private final SecurityProfileConfiguration securityConfig;

    private PrivateKey caPrivateKey;
    private X509Certificate caCertificate;
    private boolean initialized = false;
    private final SecureRandom secureRandom = new SecureRandom();

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());

        if (securityConfig.requiresTls() && !securityConfig.getKeystorePath().isEmpty()) {
            try {
                loadCACertificate();
                initialized = true;
                log.info("Certificate signing service initialized successfully");
            } catch (Exception e) {
                log.warn("Failed to initialize certificate signing service: {}. "
                        + "Certificate signing will not be available.", e.getMessage());
                initialized = false;
            }
        } else {
            log.info("Certificate signing service not initialized (TLS not configured)");
        }
    }

    private void loadCACertificate() throws Exception {
        String keystorePath = securityConfig.getKeystorePath();
        String keystorePassword = securityConfig.getKeystorePassword();
        String keystoreType = securityConfig.getKeystoreType();

        KeyStore keystore = KeyStore.getInstance(keystoreType);
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, keystorePassword.toCharArray());
        }

        String alias = keystore.aliases().nextElement();
        caPrivateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
        caCertificate = (X509Certificate) keystore.getCertificate(alias);

        log.info("Loaded CA certificate: {}", caCertificate.getSubjectX500Principal().getName());
    }

    public String signCertificateRequest(String csrPem, String chargePointId) throws Exception {
        if (!initialized) {
            throw new IllegalStateException("Certificate signing service is not initialized. "
                    + "Check TLS configuration and keystore settings.");
        }

        PKCS10CertificationRequest csr = parseCsr(csrPem);

        validateCsr(csr, chargePointId);

        X509Certificate signedCert = signCertificate(csr);

        return certificateToPem(signedCert);
    }

    public String getCertificateChain() throws Exception {
        if (!initialized || caCertificate == null) {
            throw new IllegalStateException("CA certificate not loaded");
        }

        StringBuilder chain = new StringBuilder();
        chain.append(certificateToPem(caCertificate));

        return chain.toString();
    }

    private PKCS10CertificationRequest parseCsr(String csrPem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(csrPem))) {
            Object parsedObj = pemParser.readObject();

            if (parsedObj instanceof PKCS10CertificationRequest) {
                return (PKCS10CertificationRequest) parsedObj;
            } else {
                throw new IllegalArgumentException("Invalid CSR format. Expected PKCS10 certificate request.");
            }
        }
    }

    private void validateCsr(PKCS10CertificationRequest csr, String chargePointId) throws Exception {
        if (!csr.isSignatureValid(new org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder()
                .setProvider("BC").build(csr.getSubjectPublicKeyInfo()))) {
            throw new IllegalArgumentException("CSR signature validation failed");
        }

        X500Name subject = csr.getSubject();
        RDN[] cnRdns = subject.getRDNs(BCStyle.CN);
        if (cnRdns.length == 0) {
            throw new IllegalArgumentException("CSR subject does not contain Common Name (CN)");
        }

        String csrCommonName = IETFUtils.valueToString(cnRdns[0].getFirst().getValue());
        if (!csrCommonName.equals(chargePointId)) {
            throw new IllegalArgumentException(
                String.format("CSR Common Name '%s' does not match charge point ID '%s'",
                    csrCommonName, chargePointId)
            );
        }

        log.info("CSR validated for charge point '{}'. Subject: {}",
                chargePointId, csr.getSubject().toString());
    }

    private X509Certificate signCertificate(PKCS10CertificationRequest csr) throws Exception {
        X500Name issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        X500Name subject = csr.getSubject();
        BigInteger serial = new BigInteger(64, secureRandom);
        Date notBefore = DateTime.now().toDate();
        int validityYears = securityConfig.getCertificateValidityYears();
        Date notAfter = DateTime.now().plusYears(validityYears).toDate();

        SubjectPublicKeyInfo subPubKeyInfo = csr.getSubjectPublicKeyInfo();

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, subject, subPubKeyInfo
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA")
                .setProvider("BC")
                .build(caPrivateKey);

        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);
    }

    private String certificateToPem(X509Certificate certificate) throws Exception {
        StringWriter sw = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(sw)) {
            pemWriter.writeObject(certificate);
        }
        return sw.toString();
    }

    public boolean isInitialized() {
        return initialized;
    }
}

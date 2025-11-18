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

import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import jooq.steve.db.tables.records.CertificateRecord;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;

import static de.rwth.idsg.steve.utils.CertificateUtils.certificatesToPEM;
import static de.rwth.idsg.steve.utils.CertificateUtils.parseCsr;

@Slf4j
@Service
public class CertificateSigningServiceLocal extends CertificateSigningServiceAbstract {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String PROVIDER_NAME = "BC"; // represents BouncyCastle
    private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    private final SteveProperties.Ocpp.Security securityProperties;
    private final CertificateRepository securityRepository;
    private final ChargePointService chargePointService;
    private final ChargePointServiceClient chargePointServiceClient;
    private final CertificateValidator certificateValidator;

    private final PrivateKey caPrivateKey;
    private final X509Certificate caCertificate;
    private final boolean initialized;

    private final SecureRandom secureRandom = new SecureRandom();

    public CertificateSigningServiceLocal(ServerProperties serverProperties,
                                          SteveProperties steveProperties,
                                          CertificateRepository securityRepository,
                                          ChargePointService chargePointService,
                                          ChargePointServiceClient chargePointServiceClient,
                                          CertificateValidator certificateValidator) throws Exception {
        this.securityProperties = steveProperties.getOcpp().getSecurity();
        this.securityRepository = securityRepository;
        this.chargePointService = chargePointService;
        this.chargePointServiceClient = chargePointServiceClient;
        this.certificateValidator = certificateValidator;

        Ssl ssl = serverProperties.getSsl();

        if (!securityProperties.requiresTls() || ssl.getKeyStore().isEmpty()) {
            log.info("Will not initialize (TLS not configured)");
            this.caPrivateKey = null;
            this.caCertificate = null;
            this.initialized = false;
            return;
        }

        String keystorePath = ssl.getKeyStore();
        String keystorePassword = ssl.getKeyStorePassword();
        String keystoreType = ssl.getKeyStoreType();

        KeyStore keystore = KeyStore.getInstance(keystoreType);
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, keystorePassword.toCharArray());
        }

        String alias = keystore.aliases().nextElement();
        this.caPrivateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
        this.caCertificate = (X509Certificate) keystore.getCertificate(alias);
        this.initialized = true;

        log.info("Initialized successfully. Loaded CA certificate: {}", caCertificate.getSubjectX500Principal().getName());
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected CertificateRecord signCertificate(String csrPem, String chargeBoxId) throws Exception {
        if (!initialized) {
            throw new IllegalStateException("Service is not initialized. Check configuration.");
        }

        PKCS10CertificationRequest csr = parseCsr(csrPem);
        validateCsr(csr, chargeBoxId);
        X509Certificate signedCert = signCertificate(csr);

        // Chain Order: Always order certificates from end-entity → intermediates (if any) → root
        X509Certificate[] chain = new X509Certificate[]{
            signedCert,   // newly signed certificate (from CSR)
            caCertificate // Root CA certificate
        };
        String certificateChain = certificatesToPEM(chain);

        CertificateRecord record = securityRepository.insertCertificate(signedCert, certificateChain);
        log.info("SignCertificateRequest from '{}' processed successfully. Certificate stored with ID: {}", chargeBoxId, record.getCertificateId());
        return record;
    }

    @Override
    protected void sendCertificateSignedToStation(CertificateRecord record, String chargeBoxId) {
        var params = new CertificateSignedParams();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
        params.setCertificateId(record.getCertificateId());
        params.setCertificateChain(record.getCertificateChainPem());
        chargePointServiceClient.certificateSigned(params);
    }

    private void validateCsr(PKCS10CertificationRequest csr, String chargeBoxId) throws Exception {
        {
            ContentVerifierProvider verifierProvider = new JcaContentVerifierProviderBuilder()
                .setProvider(PROVIDER_NAME)
                .build(csr.getSubjectPublicKeyInfo());

            boolean valid = csr.isSignatureValid(verifierProvider);
            if (!valid) {
                throw new IllegalArgumentException("CSR signature validation failed");
            }
        }

        X500Name subject = csr.getSubject();

        var registration = chargePointService.getRegistrationDirect(chargeBoxId);
        if (registration.isEmpty()) {
            // should never happen actually
            throw new IllegalArgumentException("Cannot find chargeBoxId=" + chargeBoxId + " in database");
        }

        certificateValidator.verifyOcppFields(registration.get(), subject);

        log.info("CSR validated for charge point '{}'", chargeBoxId);
    }

    private X509Certificate signCertificate(PKCS10CertificationRequest csr) throws Exception {
        var issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        var serial = new BigInteger(64, secureRandom);
        var notBefore = DateTime.now().toDate();
        var notAfter = DateTime.now().plusYears(securityProperties.getCertificateValidityYears()).toDate();
        var subject = csr.getSubject();
        var publicKeyInfo = csr.getSubjectPublicKeyInfo();

        var certBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);

        var signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
            .setProvider(PROVIDER_NAME)
            .build(caPrivateKey);

        var certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
            .setProvider(PROVIDER_NAME)
            .getCertificate(certHolder);
    }
}

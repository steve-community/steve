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
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.utils.CertificateUtils;
import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
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
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private final SecurityProfileConfiguration securityConfig;
    private final SecurityRepository securityRepository;
    private final ChargePointRepository chargePointRepository;
    private final ChargePointServiceClient chargePointServiceClient;

    private final PrivateKey caPrivateKey;
    private final X509Certificate caCertificate;
    private final boolean initialized;

    private final SecureRandom secureRandom = new SecureRandom();

    public CertificateSigningServiceLocal(SecurityProfileConfiguration securityConfig,
                                          SecurityRepository securityRepository,
                                          ChargePointRepository chargePointRepository,
                                          ChargePointServiceClient chargePointServiceClient) throws Exception {
        this.securityConfig = securityConfig;
        this.securityRepository = securityRepository;
        this.chargePointRepository = chargePointRepository;
        this.chargePointServiceClient = chargePointServiceClient;

        if (!securityConfig.requiresTls() || securityConfig.getKeystorePath().isEmpty()) {
            log.info("Will not initialize (TLS not configured)");
            this.caPrivateKey = null;
            this.caCertificate = null;
            this.initialized = false;
            return;
        }

        String keystorePath = securityConfig.getKeystorePath();
        String keystorePassword = securityConfig.getKeystorePassword();
        String keystoreType = securityConfig.getKeystoreType();

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
                .setProvider("BC")
                .build(csr.getSubjectPublicKeyInfo());

            boolean valid = csr.isSignatureValid(verifierProvider);
            if (!valid) {
                throw new IllegalArgumentException("CSR signature validation failed");
            }
        }

        X500Name subject = csr.getSubject();

        // A00.FR.404 :
        // The Central System SHALL verify that the certificate is owned by the CPO (or an
        // organization trusted by the CPO) by checking that the O (organizationName) RDN
        // in the subject field of the certificate contains the CPO name.
        {
            String organizationName = CertificateUtils.getOrganization(subject);
            String cpoName = getCpoName(chargeBoxId);
            if (!cpoName.equals(organizationName)) {
                throw new IllegalArgumentException("CSR signature validation failed: organizationName is not equal to CPO name");
            }
        }

        // A00.FR.405 :
        // The Central System SHALL verify that the certificate belongs to this Charge Point by
        // checking that the CN (commonName) RDN in the subject field of the certificate
        // contains the unique Serial Number of the Charge Point
        {
            String commonName = CertificateUtils.getCommonName(subject);
            String serialNumber = getSerialNumber(chargeBoxId);
            if (!serialNumber.equals(commonName)) {
                throw new IllegalArgumentException("CSR signature validation failed: commonName is not equal to Serial Number of the Charge Point");
            }
        }

        log.info("CSR validated for charge point '{}'", chargeBoxId);
    }

    private String getSerialNumber(String chargeBoxId) {
        String val = chargePointRepository.getSerialNumber(chargeBoxId);
        if (StringUtils.isEmpty(val)) {
            throw new NullPointerException("Serial number is not set for chargeBoxId: " + chargeBoxId);
        }
        return val;
    }

    /**
     * Fetches the CpoName config from the station by sending a GetConfigurationRequest and waiting for response.
     */
    private String getCpoName(String chargeBoxId) throws Exception {
        var countDownLatch = new CountDownLatch(1);

        var callback = new OcppCallback<GetConfigurationTask.ResponseWrapper>() {

            private String cpoName = null;

            @Override
            public void success(String chargeBoxId, GetConfigurationTask.ResponseWrapper response) {
                for (GetConfigurationTask.KeyValue conf : response.getConfigurationKeys()) {
                    if (ConfigurationKeyEnum.CpoName.name().equals(conf.getKey())) {
                        cpoName = conf.getValue();
                        break;
                    }
                }
                countDownLatch.countDown();
            }

            @Override
            public void success(String chargeBoxId, OcppJsonError error) {
                log.warn("Could not get configuration CpoName from charge point '{}', because: {}", chargeBoxId, error);
                countDownLatch.countDown();
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                log.warn("Could not get configuration CpoName from charge point '{}'", chargeBoxId, e);
                countDownLatch.countDown();
            }
        };

        var params = new GetConfigurationParams();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
        params.setConfKeyList(List.of(ConfigurationKeyEnum.CpoName.name()));
        chargePointServiceClient.getConfiguration(params, callback);

        countDownLatch.await(30, TimeUnit.SECONDS);

        if (callback.cpoName == null) {
            throw new NullPointerException("CpoName is null");
        }

        return callback.cpoName;
    }

    private X509Certificate signCertificate(PKCS10CertificationRequest csr) throws Exception {
        var issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        var serial = new BigInteger(64, secureRandom);
        var notBefore = DateTime.now().toDate();
        var notAfter = DateTime.now().plusYears(securityConfig.getCertificateValidityYears()).toDate();
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

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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.config.SteveProperties.Ocpp.Security.CsrSigning.LocalCsrSigning.IssuerConfig;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.utils.CertificateIssuerMaterial;
import de.rwth.idsg.steve.utils.CertificateUtils;
import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import jooq.steve.db.enums.CertificateSignatureAlgorithm;
import jooq.steve.db.tables.records.CertificateRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import static de.rwth.idsg.steve.utils.CertificateUtils.certificatesToPEM;
import static de.rwth.idsg.steve.utils.CertificateUtils.isSameKeyFamily;
import static de.rwth.idsg.steve.utils.CertificateUtils.resolveSignatureAlgorithm;
import static java.nio.charset.StandardCharsets.UTF_8;
import static jooq.steve.db.enums.CertificateSignatureAlgorithm.ECDSA;
import static jooq.steve.db.enums.CertificateSignatureAlgorithm.RSA;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

@Slf4j
@Service
@ConditionalOnProperty(
    value = "steve.ocpp.security.csr-signing.provider",
    havingValue = "local"
)
public class CertificateSigningServiceLocal implements CertificateSigningService {

    private final CertificateRepository certificateRepository;
    private final ChargePointService chargePointService;
    private final ChargePointServiceClient chargePointServiceClient;
    private final CertificateValidator certificateValidator;

    private final EnumMap<CertificateSignatureAlgorithm, CertificateIssuerMaterial> issuers = new EnumMap<>(CertificateSignatureAlgorithm.class);
    private final int certificateValidityYears;

    private final SecureRandom secureRandom = new SecureRandom();

    public CertificateSigningServiceLocal(CertificateRepository certificateRepository,
                                          ChargePointService chargePointService,
                                          ChargePointServiceClient chargePointServiceClient,
                                          CertificateValidator certificateValidator,
                                          ResourceLoader resourceLoader,
                                          SteveProperties steveProperties) throws Exception {
        this.certificateRepository = certificateRepository;
        this.chargePointService = chargePointService;
        this.chargePointServiceClient = chargePointServiceClient;
        this.certificateValidator = certificateValidator;

        var csrSigningProps = steveProperties.getOcpp().getSecurity().getCsrSigning().getProviderLocal();
        if (!csrSigningProps.isValid()) {
            throw new IllegalArgumentException("Local CSR signing is not fully configured");
        }

        loadIssuer(resourceLoader, RSA, csrSigningProps.getRsa());
        loadIssuer(resourceLoader, ECDSA, csrSigningProps.getEcdsa());

        this.certificateValidityYears = csrSigningProps.getCertificateValidityYears();
        log.info("Initialized successfully");
    }

    @Override
    public PKCS10CertificationRequest validateCSR(String csrPem, String chargeBoxId) {
        if (csrPem == null || csrPem.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty or null CSR received from " + chargeBoxId);
        }

        PKCS10CertificationRequest csr;
        try {
            csr = CertificateUtils.parseCsr(csrPem);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse CSR from " + chargeBoxId, e);
        }

        try {
            validateCsr(csr, chargeBoxId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not validate CSR from " + chargeBoxId, e);
        }

        return csr;
    }

    @Override
    public void processAndSendToStation(PKCS10CertificationRequest csr, String chargeBoxId) {
        try {
            processAndSendToStationInternal(csr, chargeBoxId);
        } catch (Exception e) {
            log.error("Exception happened for {}", chargeBoxId, e);
        }
    }

    private void processAndSendToStationInternal(PKCS10CertificationRequest csr, String chargeBoxId) throws Exception {
        var issuer = selectIssuer(csr, chargeBoxId);
        var issuerCertificateChain = issuer.issuerCertificateChain();

        X509Certificate signedCert = signCertificate(csr, issuer);

        // Chain Order: Always order certificates from end-entity → intermediates (if any) → root
        var chain = new ArrayList<X509Certificate>(1 + issuerCertificateChain.size());
        chain.add(signedCert);
        chain.addAll(issuerCertificateChain);
        String certificateChain = certificatesToPEM(chain);

        CertificateRecord record = certificateRepository.insertCertificate(signedCert, certificateChain);
        log.info("SignCertificateRequest from '{}' processed successfully. Certificate stored with ID: {}", chargeBoxId, record.getCertificateId());

        sendCertificateSignedToStation(record, chargeBoxId);
    }

    private void sendCertificateSignedToStation(CertificateRecord record, String chargeBoxId) {
        var params = new CertificateSignedParams();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
        params.setCertificateId(record.getCertificateId());
        params.setCertificateChain(record.getCertificateChainPem());
        chargePointServiceClient.certificateSigned(params);
    }

    private void validateCsr(PKCS10CertificationRequest csr, String chargeBoxId) throws Exception {
        {
            var verifierProvider = new JcaContentVerifierProviderBuilder()
                .setProvider(PROVIDER_NAME)
                .build(csr.getSubjectPublicKeyInfo());

            boolean valid = csr.isSignatureValid(verifierProvider);
            if (!valid) {
                throw new IllegalArgumentException("CSR signature validation failed");
            }
        }

        // validate CSR public key policy
        {
            var publicKey = new JcaPEMKeyConverter()
                .setProvider(PROVIDER_NAME)
                .getPublicKey(csr.getSubjectPublicKeyInfo());

            if (publicKey instanceof RSAPublicKey rsaPublicKey) {
                int bits = rsaPublicKey.getModulus().bitLength();
                if (bits < 2048) {
                    throw new IllegalArgumentException("CSR RSA key length too small. Minimum is 2048, but got " + bits);
                }
            } else if (publicKey instanceof ECPublicKey ecPublicKey) {
                int bits = ecPublicKey.getParams().getOrder().bitLength();
                if (bits < 224) {
                    throw new IllegalArgumentException("CSR EC key length too small. Minimum is 224, but got " + bits);
                }
            } else {
                throw new IllegalArgumentException("Unsupported CSR public key algorithm: " + publicKey.getAlgorithm());
            }
        }

        var registration = chargePointService.getRegistrationDirect(chargeBoxId);
        if (registration.isEmpty()) {
            // should never happen actually
            throw new IllegalArgumentException("Cannot find chargeBoxId=" + chargeBoxId + " in database");
        }

        certificateValidator.verifyOcppFields(registration.get(), csr.getSubject());

        log.info("CSR validated for charge point '{}'", chargeBoxId);
    }

    private X509Certificate signCertificate(PKCS10CertificationRequest csr, CertificateIssuerMaterial issuerMaterial) throws Exception {
        var caCertificate = issuerMaterial.caCertificate();
        var certificateSignatureAlgorithm = issuerMaterial.certificateSignatureAlgorithm();
        var caPrivateKey = issuerMaterial.caPrivateKey();

        // Preserve exact issuer DN encoding from the configured CA certificate.
        // Rebuilding from String can alter RDN ordering/encoding and break issuer matching in TLS.
        // https://stackoverflow.com/a/12645265
        var issuer = new JcaX509CertificateHolder(caCertificate).getSubject();
        var serial = new BigInteger(64, secureRandom);

        Date now = DateTime.now().toDate();
        Date notBefore = now.before(caCertificate.getNotBefore()) ? caCertificate.getNotBefore() : now;
        Date configuredNotAfter = DateTime.now().plusYears(certificateValidityYears).toDate();
        Date notAfter = configuredNotAfter.after(caCertificate.getNotAfter()) ? caCertificate.getNotAfter() : configuredNotAfter;
        if (!notAfter.after(notBefore)) {
            throw new IllegalStateException("Cannot issue certificate: resulting validity window is invalid");
        }

        var subject = csr.getSubject();
        var publicKeyInfo = csr.getSubjectPublicKeyInfo();
        PublicKey csrPublicKey = new JcaPEMKeyConverter().setProvider(PROVIDER_NAME).getPublicKey(publicKeyInfo);

        var certBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);
        addCertificateExtensions(certBuilder, publicKeyInfo, csrPublicKey, issuerMaterial);

        var signer = new JcaContentSignerBuilder(certificateSignatureAlgorithm)
            .setProvider(PROVIDER_NAME)
            .build(caPrivateKey);

        var certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
            .setProvider(PROVIDER_NAME)
            .getCertificate(certHolder);
    }

    private void addCertificateExtensions(X509v3CertificateBuilder certBuilder,
                                          SubjectPublicKeyInfo publicKeyInfo,
                                          PublicKey csrPublicKey,
                                          CertificateIssuerMaterial issuerMaterial) throws Exception {
        int keyUsageFlags = KeyUsage.digitalSignature;
        if (csrPublicKey instanceof RSAPublicKey) {
            keyUsageFlags |= KeyUsage.keyEncipherment;
        } else if (csrPublicKey instanceof ECPublicKey) {
            keyUsageFlags |= KeyUsage.keyAgreement;
        }

        var extensionUtils = new JcaX509ExtensionUtils();

        // From OCPP spec: For all certificates the X.509 Key Usage extension [9] SHOULD be used to restrict the
        // usage of the certificate to the operations for which it will be used.
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsageFlags));

        // the rest are coming from X.509/RFC rules
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils.createSubjectKeyIdentifier(publicKeyInfo));
        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(issuerMaterial.caCertificate().getPublicKey()));
    }

    private void loadIssuer(ResourceLoader resourceLoader,
                            CertificateSignatureAlgorithm name,
                            @Nullable IssuerConfig issuerConfig) throws Exception {
        if (!IssuerConfig.isValid(issuerConfig)) {
            return;
        }

        var caCertificate = resolveResource(resourceLoader, issuerConfig.getCaCertificatePem(), CertificateUtils::parseCertificate);
        var caPrivateKey = resolveResource(resourceLoader, issuerConfig.getCaKeyPem(), CertificateUtils::parsePrivateKeyViaBouncyCastle);
        var issuerCertificateChain = loadIssuerCertificateChain(resourceLoader, caCertificate, issuerConfig.getCaChainPem());
        var certificateSignatureAlgorithm = resolveSignatureAlgorithm(caPrivateKey);

        var issuer = new CertificateIssuerMaterial(
            name,
            caCertificate,
            caPrivateKey,
            issuerCertificateChain,
            certificateSignatureAlgorithm
        );

        issuer.validateFamily();
        issuer.validateCaCertificate();
        issuer.validateCertificateChain();

        issuers.put(name, issuer);

        log.info(
            "Loaded local CSR signing issuer. CA certificate: {}. Signature algorithm: {}",
            issuer.caCertificate().getSubjectX500Principal().getName(),
            issuer.certificateSignatureAlgorithm()
        );
    }

    /**
     * Future station-capability policy belongs here. Until then, use the CSR key type as a safe default
     * when both issuer families are available. A single configured issuer can sign any supported CSR key type.
     */
    private CertificateIssuerMaterial selectIssuer(PKCS10CertificationRequest csr, String chargeBoxId) throws Exception {
        if (issuers.size() == 1) {
            return issuers.values().iterator().next();
        }

        var csrPublicKey = new JcaPEMKeyConverter()
            .setProvider(PROVIDER_NAME)
            .getPublicKey(csr.getSubjectPublicKeyInfo());

        for (CertificateIssuerMaterial issuer : issuers.values()) {
            if (issuer != null && isSameKeyFamily(csrPublicKey, issuer.caPrivateKey())) {
                log.debug("Selected local CSR signing issuer '{}' for chargeBoxId '{}'", issuer.name(), chargeBoxId);
                return issuer;
            }
        }

        throw new IllegalArgumentException(
            "No local CSR signing issuer configured for CSR public key algorithm "
                + csrPublicKey.getAlgorithm()
                + " from chargeBoxId="
                + chargeBoxId
        );
    }

    private List<X509Certificate> loadIssuerCertificateChain(ResourceLoader resourceLoader,
                                                            X509Certificate caCertificate,
                                                            String caChainPem) throws Exception {
        return StringUtils.isBlank(caChainPem)
            ? List.of(caCertificate)
            : resolveResource(resourceLoader, caChainPem, CertificateUtils::parseCertificates);
    }

    private static <T> T resolveResource(ResourceLoader resourceLoader, String location, ThrowingFunction<String, T> converter) throws Exception {
        var resource = ResourceUtils.isUrl(location)
            ? resourceLoader.getResource(location)
            : resourceLoader.getResource("file:" + location);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Signing certificate PEM resource does not exist: " + location);
        }

        try (var reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            var pem = FileCopyUtils.copyToString(reader);
            return converter.apply(pem);
        }
    }

    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}

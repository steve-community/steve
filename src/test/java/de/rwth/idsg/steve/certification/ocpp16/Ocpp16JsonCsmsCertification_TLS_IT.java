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
package de.rwth.idsg.steve.certification.ocpp16;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.CertificateSignedTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import de.rwth.idsg.steve.web.dto.ocpp.ExtendedTriggerMessageParams;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateSigned;
import ocpp._2022._02.security.CertificateSignedResponse;
import ocpp._2022._02.security.ExtendedTriggerMessage;
import ocpp._2022._02.security.ExtendedTriggerMessageResponse;
import ocpp._2022._02.security.SecurityEventNotification;
import ocpp._2022._02.security.SecurityEventNotificationResponse;
import ocpp._2022._02.security.SignCertificate;
import ocpp._2022._02.security.SignCertificateResponse;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;

import static de.rwth.idsg.steve.utils.CertificateUtils.parseCertificates;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OCPP 1.6 JSON certification security somewhat intricate testcases
 * - TC_074_CSMS and TC_077_CSMS (require SignCertificate logic)
 * - TC_086_CSMS and TC_087_CSMS (server-side cert and mTLS)
 */
@Slf4j
@ActiveProfiles(profiles = {"test", "test-tls"})
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class Ocpp16JsonCsmsCertification_TLS_IT extends AbstractOcpp16JsonCsms {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String SECURE_PATH = "wss://localhost:8443/steve/websocket/CentralSystemService/";

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OcppOperationsService operationsService;
    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private TaskStore taskStore;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        log.info("----- START: {} -----", testInfo.getDisplayName());

        dslContext.settings().setExecuteLogging(false);

        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void test_TC_074_CSMS_SignCertificateRequestAccepted() throws Exception {
        var serialNumber = "SN-01-8043621";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.AUTH_PASSWORD, (String) null)
            .set(CHARGE_BOX.SECURITY_PROFILE, 3)
            .set(CHARGE_BOX.CPO_NAME, CPO_NAME)
            .set(CHARGE_BOX.CHARGE_POINT_SERIAL_NUMBER, serialNumber)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        Ssl ssl = sslWithCustomTrustStore("src/test/resources/certificates/cp-client.p12");
        var chargePoint = defaultSecureStation().startWithProfile3(ssl);

        expectGetConfCpoName(chargePoint);

        // ExtendedTriggerMessage
        {
            var params = new ExtendedTriggerMessageParams();
            params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            params.setTriggerMessage(ExtendedTriggerMessage.MessageTriggerEnumType.SIGN_CHARGE_POINT_CERTIFICATE);

            var triggerFuture = supplyAsyncUnchecked(() -> operationsService.extendedTriggerMessage(params));

            chargePoint.expectRequest(
                new ExtendedTriggerMessage()
                    .withRequestedMessage(ExtendedTriggerMessage.MessageTriggerEnumType.SIGN_CHARGE_POINT_CERTIFICATE),
                new ExtendedTriggerMessageResponse()
                    .withStatus(ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType.ACCEPTED)
            );

            var triggerStatus = successResponse(triggerFuture.join());
            assertEquals(ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType.ACCEPTED.value(), triggerStatus.value());
        }

        // SignCertificate + CertificateSigned
        var csrMaterial = createCsrMaterial(serialNumber, CPO_NAME);
        CertificateSigned arrivedRequest;
        {
            var signCertificateResponse = chargePoint.send(
                new SignCertificate().withCsr(csrMaterial.csrPem),
                SignCertificateResponse.class
            );
            assertNotNull(signCertificateResponse);
            assertEquals(SignCertificateResponse.GenericStatusEnumType.ACCEPTED, signCertificateResponse.getStatus());

            arrivedRequest = chargePoint.expectRequest(
                CertificateSigned.class,
                new CertificateSignedResponse().withStatus(CertificateSignedResponse.CertificateSignedStatusEnumType.ACCEPTED)
            );
            assertNotNull(arrivedRequest.getCertificateChain());
            assertTrue(arrivedRequest.getCertificateChain().contains("BEGIN CERTIFICATE"));

            var latestCertificateId = getCertificateIdFromTask(arrivedRequest.getCertificateChain());
            assertNotNull(latestCertificateId);

            var accepted = dslContext.select(CHARGE_BOX_CERTIFICATE_SIGNED.ACCEPTED)
                .from(CHARGE_BOX_CERTIFICATE_SIGNED)
                .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_SIGNED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
                .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
                .and(CHARGE_BOX_CERTIFICATE_SIGNED.CERTIFICATE_ID.eq(latestCertificateId))
                .orderBy(CHARGE_BOX_CERTIFICATE_SIGNED.RESPONDED_AT.desc())
                .limit(1)
                .fetchOne(CHARGE_BOX_CERTIFICATE_SIGNED.ACCEPTED);
            assertEquals(Boolean.TRUE, accepted);
        }

        chargePoint.close();

        // Build new temp client keystore
        Ssl newSsl;
        {
            var certs = parseCertificates(arrivedRequest.getCertificateChain());
            var p12Path = saveCertsToTempFile(certs, csrMaterial);

            // this new chain contains signing-ca-cert (parent) and a newly generated cert for station (child)
            String newTrustStorePath = p12Path.toAbsolutePath().toString();

            newSsl = sslWithCustomTrustStore(newTrustStorePath);
        }

        chargePoint = defaultSecureStation().startWithProfile3(newSsl);

        expectGetConfCpoName(chargePoint);

        chargePoint.close();
    }

    @Test
    public void test_TC_077_CSMS_InvalidChargePointCertificateSecurityEvent() throws InterruptedException {
        var serialNumber = "SN-01-8043621";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.AUTH_PASSWORD, (String) null)
            .set(CHARGE_BOX.SECURITY_PROFILE, 3)
            .set(CHARGE_BOX.CPO_NAME, CPO_NAME)
            .set(CHARGE_BOX.CHARGE_POINT_SERIAL_NUMBER, serialNumber)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = defaultSecureStation().startWithProfile3(serverProperties.getSsl());

        expectGetConfCpoName(chargePoint);

        // 1-2) Trigger SignChargePointCertificate
        {
            var params = new ExtendedTriggerMessageParams();
            params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            params.setTriggerMessage(ExtendedTriggerMessage.MessageTriggerEnumType.SIGN_CHARGE_POINT_CERTIFICATE);

            var triggerFuture = supplyAsyncUnchecked(() -> operationsService.extendedTriggerMessage(params));

            chargePoint.expectRequest(
                new ExtendedTriggerMessage()
                    .withRequestedMessage(ExtendedTriggerMessage.MessageTriggerEnumType.SIGN_CHARGE_POINT_CERTIFICATE),
                new ExtendedTriggerMessageResponse()
                    .withStatus(ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType.ACCEPTED)
            );

            var triggerStatus = successResponse(triggerFuture.join());
            assertEquals(ExtendedTriggerMessageResponse.TriggerMessageStatusEnumType.ACCEPTED.value(), triggerStatus.value());
        }

        // 3-4) Charge point sends SignCertificate, CSMS accepts
        {
            var signCertificateResponse = chargePoint.send(
                new SignCertificate().withCsr(createCsrMaterial(serialNumber, CPO_NAME).csrPem),
                SignCertificateResponse.class
            );
            assertNotNull(signCertificateResponse);
            assertEquals(SignCertificateResponse.GenericStatusEnumType.ACCEPTED, signCertificateResponse.getStatus());
        }

        // 5-6) CSMS sends CertificateSigned, charge point rejects
        {
            var arrivedRequest = chargePoint.expectRequest(
                CertificateSigned.class,
                new CertificateSignedResponse().withStatus(CertificateSignedResponse.CertificateSignedStatusEnumType.REJECTED)
            );
            assertNotNull(arrivedRequest.getCertificateChain());
            assertTrue(arrivedRequest.getCertificateChain().contains("BEGIN CERTIFICATE"));

            var latestCertificateId = getCertificateIdFromTask(arrivedRequest.getCertificateChain());
            assertNotNull(latestCertificateId);

            var accepted = dslContext.select(CHARGE_BOX_CERTIFICATE_SIGNED.ACCEPTED)
                .from(CHARGE_BOX_CERTIFICATE_SIGNED)
                .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_SIGNED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
                .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
                .and(CHARGE_BOX_CERTIFICATE_SIGNED.CERTIFICATE_ID.eq(latestCertificateId))
                .orderBy(CHARGE_BOX_CERTIFICATE_SIGNED.RESPONDED_AT.desc())
                .limit(1)
                .fetchOne(CHARGE_BOX_CERTIFICATE_SIGNED.ACCEPTED);
            assertEquals(Boolean.FALSE, accepted);
        }

        // 7-8) Charge point sends security event InvalidChargePointCertificate
        {
            var securityEventResponse = chargePoint.send(
                new SecurityEventNotification()
                    .withType("InvalidChargePointCertificate")
                    .withTimestamp(org.joda.time.DateTime.now()),
                SecurityEventNotificationResponse.class
            );
            assertNotNull(securityEventResponse);
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_086_CSMS_TLS_ServerSideCertificate_ValidCertificate() {
        var password = "0123456789abcdef0123456789abcdef";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.SECURITY_PROFILE, 2)
            .set(CHARGE_BOX.AUTH_PASSWORD, passwordEncoder.encode(password))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = defaultSecureStation().startWithProfile2(password, serverProperties.getSsl());

        expectGetConfCpoName(chargePoint);

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        chargePoint.close();
    }

    @Test
    public void test_TC_087_CSMS_TLS_ClientSideCertificate_ValidCertificate() {
        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.AUTH_PASSWORD, (String) null)
            .set(CHARGE_BOX.SECURITY_PROFILE, 3)
            .set(CHARGE_BOX.CPO_NAME, CPO_NAME)
            .set(CHARGE_BOX.CHARGE_POINT_SERIAL_NUMBER, "SN-01-8043621")
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = defaultSecureStation().startWithProfile3(serverProperties.getSsl());

        expectGetConfCpoName(chargePoint);

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        chargePoint.close();
    }

    private Integer getCertificateIdFromTask(String certificateChain) throws InterruptedException {
        int attempt = 0;
        int maxAttempts = 10;

        while (attempt < maxAttempts) {
            for (var task : taskStore.getFinished()) {
                if (task instanceof CertificateSignedTask certTask) {
                    if (certTask.getParams().getCertificateChain().equals(certificateChain)) {
                        return certTask.getParams().getCertificateId();
                    }
                }
            }
            attempt++;
            Thread.sleep(Duration.ofMillis(100));
        }

        return null;
    }

    private Ssl sslWithCustomTrustStore(String customTrustStore) {
        Ssl ssl = new Ssl();
        ssl.setEnabled(serverProperties.getSsl().isEnabled());

        ssl.setKeyStore(serverProperties.getSsl().getKeyStore());
        ssl.setKeyStoreType(serverProperties.getSsl().getKeyStoreType());
        ssl.setKeyStorePassword(serverProperties.getSsl().getKeyStorePassword());

        ssl.setTrustStore(customTrustStore);
        ssl.setTrustStoreType(serverProperties.getSsl().getTrustStoreType());
        ssl.setTrustStorePassword(serverProperties.getSsl().getTrustStorePassword());

        return ssl;
    }

    private record CsrMaterial(
        String csrPem,
        KeyPair keyPair
    ) {
    }

    private static CsrMaterial createCsrMaterial(String serialNumber, String cpoName) {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();

            var subject = new X500Name("CN=" + serialNumber + ",O=" + cpoName);
            var csrBuilder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
            var signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
            var csr = csrBuilder.build(signer);

            try (var writer = new StringWriter(); var pemWriter = new JcaPEMWriter(writer)) {
                pemWriter.writeObject(csr);
                pemWriter.flush();
                return new CsrMaterial(writer.toString(), keyPair);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSR", e);
        }
    }

    private static Path saveCertsToTempFile(List<X509Certificate> certs, CsrMaterial csrMaterial) throws Exception {
        var p12Path = Files.createTempFile("cp-signed-", ".p12");
        var p12Password = "changeit";

        var ks = java.security.KeyStore.getInstance("PKCS12");
        ks.load(null, null);

        // private key MUST be the CSR key
        ks.setKeyEntry(
            "cp-client-signed",
            csrMaterial.keyPair().getPrivate(),
            p12Password.toCharArray(),
            certs.toArray(new Certificate[0])
        );

        try (var out = Files.newOutputStream(p12Path)) {
            ks.store(out, p12Password.toCharArray());
        }

        return p12Path;
    }

    private static OcppJsonChargePoint defaultSecureStation() {
        return new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, SECURE_PATH);
    }
}

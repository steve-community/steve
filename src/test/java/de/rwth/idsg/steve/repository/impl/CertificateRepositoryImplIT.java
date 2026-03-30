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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.web.dto.InstalledCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.SignedCertificateQueryForm;
import ocpp._2022._02.security.CertificateHashData;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import static jooq.steve.db.tables.Certificate.CERTIFICATE;
import static jooq.steve.db.tables.ChargeBoxCertificateInstalled.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.tables.ChargeBoxCertificateSigned.CHARGE_BOX_CERTIFICATE_SIGNED;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class CertificateRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private CertificateRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void insertCertificate() {
        X509Certificate cert = testCertificate();
        var record = assertNoDatabaseException(() -> repository.insertCertificate(cert, "chain"));
        Assertions.assertNotNull(record);
        Assertions.assertNotNull(record.getCertificateId());
    }

    @Test
    public void insertCertificateSignResponse() {
        int certificateId = dslContext.insertInto(CERTIFICATE)
            .set(CERTIFICATE.CREATED_AT, DateTime.now())
            .set(CERTIFICATE.CERTIFICATE_CHAIN_PEM, "chain")
            .returning(CERTIFICATE.CERTIFICATE_ID)
            .fetchOne()
            .getCertificateId();

        assertNoDatabaseException(() -> repository.insertCertificateSignResponse(KNOWN_CHARGE_BOX_ID, certificateId, true));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_SIGNED)
            .where(CHARGE_BOX_CERTIFICATE_SIGNED.CERTIFICATE_ID.eq(certificateId))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getSignedCertificates() {
        var rows = assertNoDatabaseException(() -> repository.getSignedCertificates(new SignedCertificateQueryForm()));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getInstalledCertificateRecord() {
        Long installedId = seedInstalledCertificate();
        var row = assertNoDatabaseException(() -> repository.getInstalledCertificateRecord(installedId));
        Assertions.assertNotNull(row);
        Assertions.assertEquals(installedId, row.getId());
    }

    @Test
    public void deleteInstalledCertificate() {
        Long installedId = seedInstalledCertificate();
        assertNoDatabaseException(() -> repository.deleteInstalledCertificate(installedId));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .where(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.eq(installedId))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void deleteInstalledCertificates() {
        seedInstalledCertificate();
        assertNoDatabaseException(() -> repository.deleteInstalledCertificates(KNOWN_CHARGE_BOX_ID, "CSMSRootCertificate"));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .where(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq("CSMSRootCertificate"))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void insertInstalledCertificates() {
        var hash = certificateHashData("issuer-name-hash", "issuer-key-hash", "serial");

        assertNoDatabaseException(() -> repository.insertInstalledCertificates(
            KNOWN_CHARGE_BOX_ID, "CSMSRootCertificate", List.of(hash)
        ));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getInstalledCertificates() {
        seedInstalledCertificate();
        var rows = assertNoDatabaseException(() -> repository.getInstalledCertificates(new InstalledCertificateQueryForm()));
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    public void getInstalledCertificateIds() {
        Long installedId = seedInstalledCertificate();
        var ids = assertNoDatabaseException(() -> repository.getInstalledCertificateIds(KNOWN_CHARGE_BOX_ID));
        Assertions.assertTrue(ids.contains(installedId));
    }

    private Long seedInstalledCertificate() {
        var hash = certificateHashData(
            uniqueId("issuer-name-hash"),
            uniqueId("issuer-key-hash"),
            uniqueId("serial")
        );
        repository.insertInstalledCertificates(KNOWN_CHARGE_BOX_ID, "CSMSRootCertificate", List.of(hash));
        return dslContext.select(CHARGE_BOX_CERTIFICATE_INSTALLED.ID)
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .orderBy(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.desc())
            .limit(1)
            .fetchOne(CHARGE_BOX_CERTIFICATE_INSTALLED.ID);
    }

    private static X509Certificate testCertificate() {
        String pem = """
            -----BEGIN CERTIFICATE-----
            MIIDITCCAgmgAwIBAgIUBg5XEKTmfr1S0X2GIXp4UeDzjgwwDQYJKoZIhvcNAQEL
            BQAwIDEQMA4GA1UEAwwHU3ViamVjdDEMMAoGA1UECgwDT3JnMB4XDTI2MDMzMDE5
            NTg1NVoXDTI2MDMzMTE5NTg1NVowIDEQMA4GA1UEAwwHU3ViamVjdDEMMAoGA1UE
            CgwDT3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvoD0V7kuDa+8
            usOMipALwzJSSIrtv1XlsMpQNSswnPLLO8MhKdd6oGmEZmP1F3PWMb4nTmdNsxN7
            V5D2+Eow51lNNX6JGLXOu1IjN1+DhpejLK4m7HOjokea0tiJJSr5K1J3IuIt6nnz
            IjOM7v8Klk0UK5h7qvKxOdCw1qJUlp3xBq+Ss25CNCkyf6xP9yErqMXvaI1g3K6t
            P6BlKelLpWfeDGvtM2UvEuGzvVMYUcL045YGjEUhGtXYhBAnhnSGuHqBeHLHjNhg
            k1ZvWS3nsp30VPQIFib9sHz4ldSXEEunP6iPD5bRMkbuN9cCOtxqK1Dq0qOYNjHK
            p8KDH170PwIDAQABo1MwUTAdBgNVHQ4EFgQUgH0CDefQtxO324vx4fU76PTJX/sw
            HwYDVR0jBBgwFoAUgH0CDefQtxO324vx4fU76PTJX/swDwYDVR0TAQH/BAUwAwEB
            /zANBgkqhkiG9w0BAQsFAAOCAQEAVygi6d3yN3QINTxnJDDN42mZa86HOyL52a45
            gtMkIULNLpkTSLNKlNacnicKFwrH4T2sH6mo1w1+/4VxI/K5kRjyp6p9CWk4z1s/
            Y/+PIeWD7zFx/7LEPU7slt8M7tiIRfJefMqWChbbAGQEavmqoN9ymqgNtlxEIhvN
            n2ZRn1h1zPp3+lux0PQXqfwypvI1T2r9jDMDe6gi4x7s2dHYxrL+oWdHm697ugkm
            +gLwLNFwxIvtW4TrA+SwpVxStzg7pHY2glvwuiYtzAwoV9o2wg+509dlHaJ9MRsm
            /nyLjh1QLJLitkFwZFKMLUrKRJd63IppbxnPvNTtG6S1wBWSQw==
            -----END CERTIFICATE-----
            """;
        try {
            var factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(
                new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CertificateHashData certificateHashData(String issuerNameHash, String issuerKeyHash, String serial) {
        var hash = new CertificateHashData();
        hash.setHashAlgorithm(CertificateHashData.HashAlgorithmEnumType.SHA_256);
        hash.setIssuerNameHash(issuerNameHash);
        hash.setIssuerKeyHash(issuerKeyHash);
        hash.setSerialNumber(serial);
        return hash;
    }
}

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
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509Certificate;
import java.util.List;

import static jooq.steve.db.tables.Certificate.CERTIFICATE;
import static org.mockito.Mockito.mock;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
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
        X509Certificate cert = mock(X509Certificate.class);
        assertNoDatabaseException(() -> repository.insertCertificate(cert, "chain"));
    }

    @Test
    public void insertCertificateSignResponse() {
        Integer certificateId = dslContext.insertInto(CERTIFICATE)
            .set(CERTIFICATE.CERTIFICATE_CHAIN_PEM, "chain")
            .returning(CERTIFICATE.CERTIFICATE_ID)
            .fetchOne()
            .getCertificateId();

        assertNoDatabaseException(() -> repository.insertCertificateSignResponse(KNOWN_CHARGE_BOX_ID, certificateId, true));
    }

    @Test
    public void getSignedCertificates() {
        assertNoDatabaseException(() -> repository.getSignedCertificates(new SignedCertificateQueryForm()));
    }

    @Test
    public void getInstalledCertificateRecord() {
        assertNoDatabaseException(() -> repository.getInstalledCertificateRecord(1));
    }

    @Test
    public void deleteInstalledCertificate() {
        assertNoDatabaseException(() -> repository.deleteInstalledCertificate(1));
    }

    @Test
    public void deleteInstalledCertificates() {
        assertNoDatabaseException(() -> repository.deleteInstalledCertificates(KNOWN_CHARGE_BOX_ID, "CSMSRootCertificate"));
    }

    @Test
    public void insertInstalledCertificates() {
        assertNoDatabaseException(() -> repository.insertInstalledCertificates(
            KNOWN_CHARGE_BOX_ID, "CSMSRootCertificate", List.of()
        ));
    }

    @Test
    public void getInstalledCertificates() {
        assertNoDatabaseException(() -> repository.getInstalledCertificates(new InstalledCertificateQueryForm()));
    }

    @Test
    public void getInstalledCertificateIds() {
        assertNoDatabaseException(() -> repository.getInstalledCertificateIds(KNOWN_CHARGE_BOX_ID));
    }
}

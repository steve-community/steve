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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.InstalledCertificate;
import de.rwth.idsg.steve.web.dto.InstalledCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.SignedCertificateQueryForm;
import jooq.steve.db.tables.records.CertificateRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateInstalledRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateSignedViewRecord;
import ocpp._2022._02.security.CertificateHashData;

import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateRepository {

    // -------------------------------------------------------------------------
    // Certificate signing
    // -------------------------------------------------------------------------

    CertificateRecord insertCertificate(X509Certificate certificate, String certificateChainPEM);
    void insertCertificateSignResponse(String chargeBoxId, int certificateId, boolean accepted);
    List<ChargeBoxCertificateSignedViewRecord> getSignedCertificates(SignedCertificateQueryForm params);

    // -------------------------------------------------------------------------
    // Installed certificates
    // -------------------------------------------------------------------------

    ChargeBoxCertificateInstalledRecord getInstalledCertificateRecord(long installedCertificateId);
    void deleteInstalledCertificate(long installedCertificateId);
    void deleteInstalledCertificates(String chargeBoxId, String certificateType);
    void insertInstalledCertificates(String chargeBoxId, String certificateType, List<CertificateHashData> certificateHashData);
    List<InstalledCertificate> getInstalledCertificates(InstalledCertificateQueryForm params);

}

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

import jooq.steve.db.tables.records.CertificateRecord;
import org.slf4j.Logger;

public abstract class CertificateSigningServiceAbstract implements CertificateSigningService {

    @Override
    public void processCSR(String csrPem, String chargeBoxId) {
        try {
            CertificateRecord record = this.signCertificate(csrPem, chargeBoxId);
            this.sendCertificateSignedToStation(record, chargeBoxId);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
        }
    }

    protected abstract Logger getLog();

    protected abstract CertificateRecord signCertificate(String csrPem, String chargeBoxId) throws Exception;

    protected abstract void sendCertificateSignedToStation(CertificateRecord record, String chargeBoxId);
}

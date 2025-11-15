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

import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.utils.CertificateUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

@Slf4j
@Component
public class CertificateValidator {

    public void verifyOcppFields(ChargePointRegistration chargePointRegistration, X509Certificate cert) {
        var x500name = new X500Name(cert.getSubjectX500Principal().getName());
        verifyOcppFields(chargePointRegistration, x500name);
    }

    public void verifyOcppFields(ChargePointRegistration chargePointRegistration, X500Name subject) {
        // A00.FR.404 :
        // The Central System SHALL verify that the certificate is owned by the CPO (or an
        // organization trusted by the CPO) by checking that the O (organizationName) RDN
        // in the subject field of the certificate contains the CPO name.
        {
            String organizationName = CertificateUtils.getOrganization(subject);
            String cpoName = chargePointRegistration.cpoName();
            if (StringUtils.isEmpty(cpoName)) {
                throw new NullPointerException("CPO name is not set for chargeBoxId: " + chargePointRegistration.chargeBoxId());
            }
            if (!cpoName.equals(organizationName)) {
                throw new IllegalArgumentException("Validation failed: organizationName is not equal to CPO name");
            }
        }

        // A00.FR.405 :
        // The Central System SHALL verify that the certificate belongs to this Charge Point by
        // checking that the CN (commonName) RDN in the subject field of the certificate
        // contains the unique Serial Number of the Charge Point
        {
            String commonName = CertificateUtils.getCommonName(subject);
            String serialNumber = chargePointRegistration.serialNumber();
            if (StringUtils.isEmpty(serialNumber)) {
                throw new NullPointerException("Serial number is not set for chargeBoxId: " + chargePointRegistration.chargeBoxId());
            }
            if (!serialNumber.equals(commonName)) {
                throw new IllegalArgumentException("Validation failed: commonName is not equal to Serial Number of the Charge Point");
            }
        }
    }
}

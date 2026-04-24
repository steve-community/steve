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
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.utils.CertificateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;
import java.security.cert.X509Certificate;

/**
 * The implementation of this validator ASSUMES external components to do the standard certificate validation already.
 * We delegate and outsource this responsibility.
 * <p>
 * - When running the app standalone: Spring Boot needs proper and complete configuration regarding keystore and
 * truststore. See <code>server.ssl</code> of application.yml
 * <p>
 * - When running the app in a setup with reverse proxy (or other components in front of the communication chain): These
 * components need proper and complete configuration regarding mTLS. Moreover, they MUST be configured to forward the
 * client cert as header to this app for us to do some other checks as well. The name of the header is configurable at
 * <code>steve.ocpp.security.client-cert-header-from-proxy</code> of application.yml. This option has precedence over
 * the previous bullet point in the flow. If this config has a value, we consider this an active decision and will NOT
 * fall back to previous bullet point, if we cannot find the cert in the header.
 * <p>
 * In both cases, the management/update of certs in keystore and truststore is the concern of the operator.
 */
@Slf4j
@Component
public class CertificateValidator {

    private final String clientCertHeaderFromProxy;

    public CertificateValidator(SteveProperties steveProperties) {
        clientCertHeaderFromProxy = steveProperties.getOcpp().getSecurity().getClientCertHeaderFromProxy();
    }

    /**
     * We have multiple options to get cert:
     * 1. Get from request header (if forwarded by a reverse proxy)
     * 2. Get from servlet request
     * 3. Get from spring security
     */
    @Nullable
    public X509Certificate getCertificate(ServerHttpRequest request, String chargeBoxId) {
        if (!StringUtils.isEmpty(clientCertHeaderFromProxy)) {
            String certPem = request.getHeaders().getFirst(clientCertHeaderFromProxy);
            if (!StringUtils.isEmpty(certPem)) {
                try {
                    log.debug("ChargeBoxId '{}': Found cert in HTTP headers under configured header", chargeBoxId);
                    return CertificateUtils.parseCertificate(certPem);
                } catch (Exception e) {
                    log.warn("ChargeBoxId '{}': Failed parsing the certificate from header", chargeBoxId, e);
                    return null;
                }
            }
        }

        // https://stackoverflow.com/questions/39471044/spring-boot-reading-x509-client-certificate-from-http-request
        ServletServerHttpRequest casted = (ServletServerHttpRequest) request;
        X509Certificate[] certs = (X509Certificate[]) casted.getServletRequest().getAttribute("jakarta.servlet.request.X509Certificate");
        if (certs != null) {
            log.debug("ChargeBoxId '{}': Found cert in ServletServerHttpRequest", chargeBoxId);
            return certs[0];
        }

        // https://stackoverflow.com/a/26844985
        Object credentialsObject = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if (credentialsObject instanceof X509Certificate cert) {
            log.debug("ChargeBoxId '{}': Found cert in SecurityContext of Spring", chargeBoxId);
            return cert;
        }

        log.debug("ChargeBoxId '{}': Could not find cert", chargeBoxId);
        return null;
    }

    public boolean validate(ChargePointRegistration chargePointRegistration, X509Certificate certificate) {
        String chargeBoxId = chargePointRegistration.chargeBoxId();

        if (certificate == null) {
            log.error("ChargeBoxId '{}': Certificate is null", chargeBoxId);
            return false;
        }

        try {
            // Do OCPP-specific checks
            verifyOcppFields(chargePointRegistration, certificate);
            log.debug("ChargeBoxId '{}': Successful cert validation", chargeBoxId);
            return true;
        } catch (Exception e) {
            log.error("ChargeBoxId '{}': Certificate validation error", chargeBoxId, e);
            return false;
        }
    }

    public void verifyOcppFields(ChargePointRegistration chargePointRegistration, X509Certificate cert) {
        var x500name = new X500Name(cert.getSubjectX500Principal().getName());
        verifyOcppFields(chargePointRegistration, x500name);
    }

    public void verifyOcppFields(ChargePointRegistration chargePointRegistration, X500Name subject) {
        String chargeBoxId = chargePointRegistration.chargeBoxId();

        // A00.FR.404 :
        // The Central System SHALL verify that the certificate is owned by the CPO (or an
        // organization trusted by the CPO) by checking that the O (organizationName) RDN
        // in the subject field of the certificate contains the CPO name.
        {
            String organizationName = CertificateUtils.getOrganization(subject);
            if (StringUtils.isEmpty(organizationName)) {
                throw new IllegalArgumentException("Validation failed: O (organizationName) is empty");
            }

            String cpoName = chargePointRegistration.cpoName();
            if (StringUtils.isEmpty(cpoName)) {
                throw new IllegalArgumentException("CPO name is not set for chargeBoxId: " + chargeBoxId);
            }

            if (!organizationName.contains(cpoName)) {
                throw new IllegalArgumentException("Validation failed: organizationName does not contain CPO name");
            }
            log.debug("ChargeBoxId '{}': Successful OrganizationName (CPO name) validation", chargeBoxId);
        }

        // A00.FR.405 :
        // The Central System SHALL verify that the certificate belongs to this Charge Point by
        // checking that the CN (commonName) RDN in the subject field of the certificate
        // contains the unique Serial Number of the Charge Point
        {
            String commonName = CertificateUtils.getCommonName(subject);
            if (StringUtils.isEmpty(commonName)) {
                throw new IllegalArgumentException("Validation failed: CN (commonName) is empty");
            }

            String serialNumber = chargePointRegistration.serialNumber();
            if (StringUtils.isEmpty(serialNumber)) {
                throw new IllegalArgumentException("Serial number is not set for chargeBoxId: " + chargeBoxId);
            }

            if (StringUtils.isEmpty(commonName) || !commonName.contains(serialNumber)) {
                throw new IllegalArgumentException("Validation failed: commonName does not contain Serial Number of the Charge Point");
            }
            log.debug("ChargeBoxId '{}': Successful CommonName (serial number) validation", chargeBoxId);
        }
    }
}

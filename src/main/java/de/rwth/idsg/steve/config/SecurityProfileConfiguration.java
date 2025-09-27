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
package de.rwth.idsg.steve.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Getter
@Configuration
public class SecurityProfileConfiguration {

    @Value("${ocpp.security.profile:0}")
    private int securityProfile;

    @Value("${ocpp.security.tls.enabled:false}")
    private boolean tlsEnabled;

    @Value("${ocpp.security.tls.keystore.path:}")
    private String keystorePath;

    @Value("${ocpp.security.tls.keystore.password:}")
    private String keystorePassword;

    @Value("${ocpp.security.tls.keystore.type:JKS}")
    private String keystoreType;

    @Value("${ocpp.security.tls.truststore.path:}")
    private String truststorePath;

    @Value("${ocpp.security.tls.truststore.password:}")
    private String truststorePassword;

    @Value("${ocpp.security.tls.truststore.type:JKS}")
    private String truststoreType;

    @Value("${ocpp.security.tls.client.auth:false}")
    private boolean clientAuthRequired;

    @Value("${ocpp.security.tls.protocols:TLSv1.2,TLSv1.3}")
    private String[] tlsProtocols;

    @Value("${ocpp.security.tls.ciphers:}")
    private String[] tlsCipherSuites;

    @Value("${ocpp.security.certificate.validity.years:1}")
    private int certificateValidityYears;

    @PostConstruct
    public void init() {
        log.info("OCPP Security Profile Configuration:");
        log.info("  Security Profile: {}", securityProfile);
        log.info("  TLS Enabled: {}", tlsEnabled);

        if (tlsEnabled) {
            log.info("  Keystore Path: {}", keystorePath.isEmpty() ? "(not configured)" : keystorePath);
            log.info("  Keystore Type: {}", keystoreType);
            log.info("  Truststore Path: {}", truststorePath.isEmpty() ? "(not configured)" : truststorePath);
            log.info("  Truststore Type: {}", truststoreType);
            log.info("  Client Auth Required: {}", clientAuthRequired);
            log.info("  TLS Protocols: {}", String.join(", ", tlsProtocols));

            if (tlsCipherSuites != null && tlsCipherSuites.length > 0) {
                log.info("  Cipher Suites: {}", String.join(", ", tlsCipherSuites));
            }

            validateConfiguration();
        }
    }

    private void validateConfiguration() {
        if (securityProfile >= 2 && keystorePath.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                        "Security Profile %d requires TLS but 'ocpp.security.tls.keystore.path' is not configured",
                        securityProfile)
            );
        }

        if (securityProfile >= 3 && !clientAuthRequired) {
            log.warn("Security Profile 3 is configured but client certificate authentication is disabled. "
                    + "Set 'ocpp.security.tls.client.auth=true' for proper mTLS security.");
        }

        if (clientAuthRequired && truststorePath.isEmpty()) {
            throw new IllegalStateException(
                "Client certificate authentication is enabled but 'ocpp.security.tls.truststore.path' is not configured"
            );
        }
    }

    public boolean isProfile0() {
        return securityProfile == 0;
    }

    public boolean isProfile1() {
        return securityProfile == 1;
    }

    public boolean isProfile2() {
        return securityProfile == 2;
    }

    public boolean isProfile3() {
        return securityProfile == 3;
    }

    public boolean requiresTls() {
        return securityProfile >= 2;
    }

    public boolean requiresClientCertificate() {
        return securityProfile >= 3;
    }
}

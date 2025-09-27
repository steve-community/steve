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
package de.rwth.idsg.steve.gateway.ocpi.controller;

import de.rwth.idsg.steve.gateway.ocpi.model.Credentials;
import de.rwth.idsg.steve.gateway.ocpi.model.OcpiResponse;
import de.rwth.idsg.steve.gateway.repository.GatewayPartnerRepository;
import de.rwth.idsg.steve.gateway.security.TokenEncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jooq.steve.db.enums.GatewayPartnerProtocol;
import jooq.steve.db.enums.GatewayPartnerRole;
import jooq.steve.db.tables.records.GatewayPartnerRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi/2.2/credentials", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OCPI Credentials", description = "OCPI v2.2 Credentials API - Partner registration and token exchange")
public class CredentialsController {

    private final GatewayPartnerRepository partnerRepository;
    private final TokenEncryptionService encryptionService;

    @Value("${steve.gateway.base-url:http://localhost:8080}")
    private String baseUrl;

    @GetMapping
    @Operation(summary = "Get credentials", description = "Retrieve current credentials for the authenticated partner")
    public OcpiResponse<Credentials> getCredentials(Authentication authentication) {
        log.debug("Get credentials request");

        GatewayPartnerRecord partner = (GatewayPartnerRecord) authentication.getPrincipal();

        Credentials credentials = Credentials.builder()
            .token(encryptionService.encrypt(partner.getToken()))
            .url(baseUrl + "/ocpi/versions")
            .roles(java.util.List.of(
                Credentials.CredentialsRole.builder()
                    .role(partner.getRole().getLiteral())
                    .businessDetails(Credentials.BusinessDetails.builder()
                        .name(partner.getName())
                        .build())
                    .partyId(partner.getPartyId())
                    .countryCode(partner.getCountryCode())
                    .build()
            ))
            .build();

        return OcpiResponse.success(credentials);
    }

    @PostMapping
    @Operation(summary = "Register credentials", description = "Register new credentials and generate a new authentication token")
    public OcpiResponse<Credentials> registerCredentials(
        @RequestBody Credentials credentials,
        Authentication authentication
    ) {
        log.debug("Register credentials request: {}", credentials);

        GatewayPartnerRecord partner = (GatewayPartnerRecord) authentication.getPrincipal();

        String newToken = UUID.randomUUID().toString();
        partner.setToken(newToken);

        if (!credentials.getRoles().isEmpty()) {
            Credentials.CredentialsRole role = credentials.getRoles().get(0);
            partner.setPartyId(role.getPartyId());
            partner.setCountryCode(role.getCountryCode());

            if (role.getRole() != null) {
                partner.setRole(GatewayPartnerRole.valueOf(role.getRole()));
            }
        }

        partner.store();

        Credentials response = Credentials.builder()
            .token(encryptionService.encrypt(newToken))
            .url(baseUrl + "/ocpi/versions")
            .roles(java.util.List.of(
                Credentials.CredentialsRole.builder()
                    .role(partner.getRole().getLiteral())
                    .businessDetails(Credentials.BusinessDetails.builder()
                        .name(partner.getName())
                        .build())
                    .partyId(partner.getPartyId())
                    .countryCode(partner.getCountryCode())
                    .build()
            ))
            .build();

        return OcpiResponse.success(response);
    }

    @PutMapping
    @Operation(summary = "Update credentials", description = "Update existing credentials for the authenticated partner")
    public OcpiResponse<Credentials> updateCredentials(
        @RequestBody Credentials credentials,
        Authentication authentication
    ) {
        log.debug("Update credentials request: {}", credentials);

        GatewayPartnerRecord partner = (GatewayPartnerRecord) authentication.getPrincipal();

        if (!credentials.getRoles().isEmpty()) {
            Credentials.CredentialsRole role = credentials.getRoles().get(0);
            partner.setPartyId(role.getPartyId());
            partner.setCountryCode(role.getCountryCode());

            if (role.getRole() != null) {
                partner.setRole(GatewayPartnerRole.valueOf(role.getRole()));
            }
        }

        partner.store();

        Credentials response = Credentials.builder()
            .token(encryptionService.encrypt(partner.getToken()))
            .url(baseUrl + "/ocpi/versions")
            .roles(java.util.List.of(
                Credentials.CredentialsRole.builder()
                    .role(partner.getRole().getLiteral())
                    .businessDetails(Credentials.BusinessDetails.builder()
                        .name(partner.getName())
                        .build())
                    .partyId(partner.getPartyId())
                    .countryCode(partner.getCountryCode())
                    .build()
            ))
            .build();

        return OcpiResponse.success(response);
    }

    @DeleteMapping
    @Operation(summary = "Delete credentials", description = "Revoke credentials and disable partner access")
    public OcpiResponse<Void> deleteCredentials(Authentication authentication) {
        log.debug("Delete credentials request");

        GatewayPartnerRecord partner = (GatewayPartnerRecord) authentication.getPrincipal();

        partner.setEnabled(false);
        partner.store();

        return OcpiResponse.success(null);
    }
}
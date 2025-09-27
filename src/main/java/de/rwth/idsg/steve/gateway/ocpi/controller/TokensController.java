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

import de.rwth.idsg.steve.gateway.adapter.OcppToOcpiAdapter;
import de.rwth.idsg.steve.gateway.ocpi.model.AuthorizationInfo;
import de.rwth.idsg.steve.gateway.ocpi.model.LocationReferences;
import de.rwth.idsg.steve.gateway.ocpi.model.OcpiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OCPI v2.2 Tokens REST Controller
 * Handles token authorization requests from EMSP
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi/emsp/2.2/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OCPI Tokens", description = "OCPI v2.2 Tokens API - Token authorization for charging access")
public class TokensController {

    private final OcppToOcpiAdapter ocppToOcpiAdapter;

    @PostMapping("/authorize")
    @Operation(summary = "Authorize token", description = "Verify if a token is authorized to charge at the specified location")
    public OcpiResponse<AuthorizationInfo> authorizeToken(@RequestBody LocationReferences locationReferences) {
        log.debug("Token authorization request: {}", locationReferences);

        try {
            AuthorizationInfo authInfo = ocppToOcpiAdapter.authorizeToken(locationReferences);
            return OcpiResponse.success(authInfo);
        } catch (Exception e) {
            log.error("Error during token authorization", e);
            return OcpiResponse.error(2000, "Unable to authorize token");
        }
    }
}
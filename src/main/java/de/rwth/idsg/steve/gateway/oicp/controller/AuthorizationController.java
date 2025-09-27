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
package de.rwth.idsg.steve.gateway.oicp.controller;

import de.rwth.idsg.steve.gateway.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStart;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStop;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStartResponse;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStopResponse;
import de.rwth.idsg.steve.gateway.oicp.model.OicpResponse;
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
 * OICP v2.3 Authorization REST Controller
 * Handles authorization start and stop requests
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/oicp/authorization/v23", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OICP Authorization", description = "OICP v2.3 Authorization API - Remote authorization for charging sessions")
public class AuthorizationController {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @PostMapping("/operators/{operatorId}/authorize/start")
    @Operation(summary = "Authorize charging start", description = "Request authorization to start a charging session")
    public OicpResponse<AuthorizationStartResponse> authorizeStart(@RequestBody AuthorizationStart request) {
        log.debug("Authorization start request: {}", request);

        try {
            AuthorizationStartResponse response = ocppToOicpAdapter.authorizeStart(request);
            return OicpResponse.success(response);
        } catch (Exception e) {
            log.error("Error during authorization start", e);
            return OicpResponse.error("6000", "Authorization failed");
        }
    }

    @PostMapping("/operators/{operatorId}/authorize/stop")
    @Operation(summary = "Authorize charging stop", description = "Request authorization to stop a charging session")
    public OicpResponse<AuthorizationStopResponse> authorizeStop(@RequestBody AuthorizationStop request) {
        log.debug("Authorization stop request: {}", request);

        try {
            AuthorizationStopResponse response = ocppToOicpAdapter.authorizeStop(request);
            return OicpResponse.success(response);
        } catch (Exception e) {
            log.error("Error during authorization stop", e);
            return OicpResponse.error("6000", "Authorization stop failed");
        }
    }
}
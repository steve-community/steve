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

import de.rwth.idsg.steve.gateway.oicp.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEStatusByID;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEvseStatusV21200Response;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEvseStatusV21Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.errorCode;
import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.toResponse;

/**
 * OICP v2.3 EVSE Status REST Controller
 * Provides EVSE status information for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class EvseStatusController implements ERoamingEvseStatusApi {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @Override
    public ResponseEntity<ERoamingPullEvseStatusV21200Response> eRoamingPullEvseStatusV21(
            String providerID, ERoamingPullEvseStatusV21Request request) {
        log.debug("Get EVSE status request for providerID: {}, request: {}", providerID, request);

        try {
            return toResponse(ocppToOicpAdapter.getEVSEStatus(providerID, request));
        } catch (Exception e) {
            log.error("Error retrieving EVSE status for provider: {}", providerID, e);
            return toResponse(ERoamingEVSEStatusByID.builder()
                    .statusCode(errorCode("3000", "Unable to retrieve EVSE status"))
                    .build());
        }
    }
}

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
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEVSEData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.errorCode;
import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.toResponse;

/**
 * OICP v2.3 EVSE Data REST Controller
 * Provides EVSE data information for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class EvseDataController implements ERoamingEvseDataApi {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @Override
    public ResponseEntity<ERoamingEVSEData> eRoamingPullEvseDataV23(String providerID, ERoamingPullEVSEData request) {
        log.debug("Get EVSE data request for providerID: {}, request: {}", providerID, request);

        try {
            return toResponse(ocppToOicpAdapter.getEVSEData(providerID, request));
        } catch (Exception e) {
            log.error("Error retrieving EVSE data for operator: {}", providerID, e);
            return toResponse(ERoamingEVSEData.builder()
                    .statusCode(errorCode("3000", "Unable to retrieve EVSE data"))
                    .build());
        }
    }
}

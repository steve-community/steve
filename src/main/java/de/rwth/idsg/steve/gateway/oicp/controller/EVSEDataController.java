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
import de.rwth.idsg.steve.gateway.oicp.model.EVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRecord;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEDataRequest;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRequest;
import de.rwth.idsg.steve.gateway.oicp.model.OicpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OICP v2.3 EVSE Data REST Controller
 * Provides EVSE data and status information for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/oicp/evsepull/v23", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OICP EVSE Data", description = "OICP v2.3 EVSE Data API - Electric Vehicle Supply Equipment information")
public class EVSEDataController {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @PostMapping("/operators/{operatorId}/data-records")
    @Operation(summary = "Get EVSE data records", description = "Retrieve EVSE data records for a specific operator with optional filtering")
    public OicpResponse<List<EVSEData>> getEVSEData(
            @Parameter(description = "Operator identifier") @RequestParam String operatorId,
            @RequestBody(required = false) EVSEDataRequest request) {

        log.debug("Get EVSE data request for operatorId: {}, request: {}", operatorId, request);

        try {
            List<EVSEData> evseDataList = ocppToOicpAdapter.getEVSEData(operatorId, request);
            return OicpResponse.success(evseDataList);
        } catch (Exception e) {
            log.error("Error retrieving EVSE data for operator: {}", operatorId, e);
            return OicpResponse.error("3000", "Unable to retrieve EVSE data");
        }
    }

    @PostMapping("/operators/{operatorId}/status-records")
    @Operation(summary = "Get EVSE status records", description = "Retrieve real-time status information for EVSEs of a specific operator")
    public OicpResponse<List<EVSEStatusRecord>> getEVSEStatus(
            @Parameter(description = "Operator identifier") @RequestParam String operatorId,
            @RequestBody(required = false) EVSEStatusRequest request) {

        log.debug("Get EVSE status request for operatorId: {}, request: {}", operatorId, request);

        try {
            List<EVSEStatusRecord> statusRecords = ocppToOicpAdapter.getEVSEStatus(operatorId, request);
            return OicpResponse.success(statusRecords);
        } catch (Exception e) {
            log.error("Error retrieving EVSE status for operator: {}", operatorId, e);
            return OicpResponse.error("3000", "Unable to retrieve EVSE status");
        }
    }
}
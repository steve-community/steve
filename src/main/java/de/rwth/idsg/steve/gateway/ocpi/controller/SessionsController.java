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
import de.rwth.idsg.steve.gateway.ocpi.model.Session;
import de.rwth.idsg.steve.gateway.ocpi.model.OcpiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OCPI v2.2 Sessions REST Controller
 * Provides access to charging session information for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi/cpo/2.2/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OCPI Sessions", description = "OCPI v2.2 Sessions API - Charging session data for roaming")
public class SessionsController {

    private final OcppToOcpiAdapter ocppToOcpiAdapter;

    @GetMapping
    @Operation(summary = "Get all sessions", description = "Retrieve a list of all charging sessions with optional date filtering and pagination")
    public OcpiResponse<List<Session>> getSessions(
            @Parameter(description = "Filter sessions updated after this date (ISO 8601 format)") @RequestParam(required = false) String dateFrom,
            @Parameter(description = "Filter sessions updated before this date (ISO 8601 format)") @RequestParam(required = false) String dateTo,
            @Parameter(description = "Pagination offset") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Maximum number of sessions to return") @RequestParam(defaultValue = "100") int limit) {

        log.debug("Get sessions request - dateFrom: {}, dateTo: {}, offset: {}, limit: {}",
                  dateFrom, dateTo, offset, limit);

        try {
            List<Session> sessions = ocppToOcpiAdapter.getSessions(dateFrom, dateTo, offset, limit);
            return OcpiResponse.success(sessions);
        } catch (Exception e) {
            log.error("Error retrieving sessions", e);
            return OcpiResponse.error(2000, "Unable to retrieve sessions");
        }
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session by ID", description = "Retrieve detailed information about a specific charging session")
    public OcpiResponse<Session> getSession(
            @Parameter(description = "Unique session identifier") @PathVariable String sessionId) {
        log.debug("Get session request for sessionId: {}", sessionId);

        try {
            Session session = ocppToOcpiAdapter.getSession(sessionId);
            if (session != null) {
                return OcpiResponse.success(session);
            } else {
                return OcpiResponse.error(2003, "Session not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving session: {}", sessionId, e);
            return OcpiResponse.error(2000, "Unable to retrieve session");
        }
    }
}
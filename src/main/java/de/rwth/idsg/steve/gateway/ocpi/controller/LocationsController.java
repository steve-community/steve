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
import de.rwth.idsg.steve.gateway.ocpi.model.Connector;
import de.rwth.idsg.steve.gateway.ocpi.model.EVSE;
import de.rwth.idsg.steve.gateway.ocpi.model.Location;
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
 * OCPI v2.2 Locations REST Controller
 * Provides access to location information for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi/cpo/2.2/locations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OCPI Locations", description = "OCPI v2.2 Locations API - Charge point location data for roaming")
public class LocationsController {

    private final OcppToOcpiAdapter ocppToOcpiAdapter;

    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieve a list of all charging locations with optional date filtering and pagination")
    public OcpiResponse<List<Location>> getLocations(
            @Parameter(description = "Filter locations updated after this date (ISO 8601 format)") @RequestParam(required = false) String dateFrom,
            @Parameter(description = "Filter locations updated before this date (ISO 8601 format)") @RequestParam(required = false) String dateTo,
            @Parameter(description = "Pagination offset") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Maximum number of locations to return") @RequestParam(defaultValue = "100") int limit) {

        log.debug("Get locations request - dateFrom: {}, dateTo: {}, offset: {}, limit: {}",
                  dateFrom, dateTo, offset, limit);

        try {
            List<Location> locations = ocppToOcpiAdapter.getLocations(dateFrom, dateTo, offset, limit);
            return OcpiResponse.success(locations);
        } catch (Exception e) {
            log.error("Error retrieving locations", e);
            return OcpiResponse.error(2000, "Unable to retrieve locations");
        }
    }

    @GetMapping("/{locationId}")
    @Operation(summary = "Get location by ID", description = "Retrieve detailed information about a specific charging location")
    public OcpiResponse<Location> getLocation(
            @Parameter(description = "Unique location identifier") @PathVariable String locationId) {
        log.debug("Get location request for locationId: {}", locationId);

        try {
            Location location = ocppToOcpiAdapter.getLocation(locationId);
            if (location != null) {
                return OcpiResponse.success(location);
            } else {
                return OcpiResponse.error(2003, "Location not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving location: {}", locationId, e);
            return OcpiResponse.error(2000, "Unable to retrieve location");
        }
    }

    @GetMapping("/{locationId}/{evseUid}")
    @Operation(summary = "Get EVSE by ID", description = "Retrieve detailed information about a specific Electric Vehicle Supply Equipment (EVSE) at a location")
    public OcpiResponse<EVSE> getEvse(
            @Parameter(description = "Location identifier") @PathVariable String locationId,
            @Parameter(description = "Unique EVSE identifier") @PathVariable String evseUid) {
        log.debug("Get EVSE request for locationId: {}, evseUid: {}", locationId, evseUid);

        try {
            EVSE evse = ocppToOcpiAdapter.getEvse(locationId, evseUid);
            if (evse != null) {
                return OcpiResponse.success(evse);
            } else {
                return OcpiResponse.error(2003, "EVSE not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving EVSE: {}/{}", locationId, evseUid, e);
            return OcpiResponse.error(2000, "Unable to retrieve EVSE");
        }
    }

    @GetMapping("/{locationId}/{evseUid}/{connectorId}")
    @Operation(summary = "Get connector by ID", description = "Retrieve detailed information about a specific connector on an EVSE")
    public OcpiResponse<Connector> getConnector(
            @Parameter(description = "Location identifier") @PathVariable String locationId,
            @Parameter(description = "EVSE identifier") @PathVariable String evseUid,
            @Parameter(description = "Connector identifier") @PathVariable String connectorId) {
        log.debug("Get Connector request for locationId: {}, evseUid: {}, connectorId: {}",
                  locationId, evseUid, connectorId);

        try {
            Connector connector = ocppToOcpiAdapter.getConnector(locationId, evseUid, connectorId);
            if (connector != null) {
                return OcpiResponse.success(connector);
            } else {
                return OcpiResponse.error(2003, "Connector not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving Connector: {}/{}/{}", locationId, evseUid, connectorId, e);
            return OcpiResponse.error(2000, "Unable to retrieve Connector");
        }
    }
}
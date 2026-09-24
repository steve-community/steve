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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.web.dto.ConnectorStatusList;
import de.rwth.idsg.steve.web.dto.WebSocketConnectionList;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.Statistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "statistics-controller")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatisticsRestController {

    private final ChargePointService chargePointService;

    @Operation(description = """
        Returns some simple business statistics of the system.
        """)
    @GetMapping(value = "")
    public Statistics getStatistics() {
        return chargePointService.getStats();
    }

    @Operation(description = """
        Returns the latest status (and optionally the error code) information of connectors of Charge Points.
        The status information can be filtered by the given parameters.
        """)
    @GetMapping(value = "/status")
    public ConnectorStatusList getConnectorStatus(@Valid @ParameterObject ConnectorStatusForm params) {
        List<ConnectorStatus> latestList = chargePointService.getChargePointConnectorStatus(params);
        List<ConnectorStatus> filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);
        return new ConnectorStatusList(filteredList);
    }

    @Operation(description = """
        Returns the list of all connected WS/JSON Charge Points (i.e. Charge Points that have an open WebSocket connection to our platform at the moment).
        """)
    @GetMapping(value = "/connected")
    public WebSocketConnectionList getOcppJsonStatus() {
        var statusList = chargePointService.getOcppJsonStatus();
        return new WebSocketConnectionList(statusList);
    }
}

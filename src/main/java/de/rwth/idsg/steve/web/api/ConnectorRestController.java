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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter;
import de.rwth.idsg.steve.web.api.dto.ApiConnectorList;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fnkbsi
 * @since 20.10.2023
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/connectors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConnectorRestController {

    private final ChargePointRepository chargePointRepository;
    private final ChargePointHelperService chargePointHelperService;

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @StandardApiResponses
    @GetMapping(value = "")
    public ApiConnectorList getConnectors(@Valid ConnectorStatusForm queryParams) {
        var conList = new ApiConnectorList();
        conList.setChargeBoxList(chargePointRepository.getChargeBoxIds());

        conList.setFiltered(isFiltered(queryParams));
        var latestList = chargePointHelperService.getChargePointConnectorStatus(queryParams);
        List<ConnectorStatus> sortedList;
        if (queryParams.getStrategy() == ConnectorStatusForm.Strategy.PreferZero) {
            sortedList = ConnectorStatusFilter.filterAndPreferZero(latestList);
        } else {
            sortedList = ConnectorStatusFilter.filterAndPreferOthersWithStatusOfZero(latestList);
        }
        conList.setConnectors(sortedList);
        return conList;
    }

    @StandardApiResponses
    @GetMapping(value = "OCPP_JSON_STATUS")
    public List<OcppJsonStatus> getOcppJsonStatus() {
        return chargePointHelperService.getOcppJsonStatus();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean isFiltered(ConnectorStatusForm queryParams) {
        if (queryParams == null) {
            return false;
        }
        return isDefined(queryParams.getChargeBoxId())
          || isDefined(queryParams.getStatus());
    }

    private static boolean isDefined(String str) {
        return str != null && !str.isBlank();
    }
}

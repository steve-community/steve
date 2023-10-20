/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import static java.util.Objects.isNull;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author fnkbsi
 * since 20.10.2023
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/connectors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ConnectorRestController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private ChargePointHelperService chargePointHelperService;

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiControllerAdvice.ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiControllerAdvice.ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiControllerAdvice.ApiErrorResponse.class)}
    )
    @GetMapping(value = "")
    @ResponseBody
    public ApiConnectorList getConnectorStatusQuery(@Valid ConnectorStatusForm queryParams) {
        ApiConnectorList conList = new ApiConnectorList();
        conList.setChargeBoxList(chargePointRepository.getChargeBoxIds());

        conList.setIsFiltered(isFilterd(queryParams));
        List<ConnectorStatus> latestList = chargePointHelperService.getChargePointConnectorStatus(queryParams);
        List<ConnectorStatus> sortedList = ConnectorStatusFilter.filterAndPreferZero(latestList);
        conList.setConnectors(sortedList);
        return conList;
    }

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiControllerAdvice.ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiControllerAdvice.ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiControllerAdvice.ApiErrorResponse.class)}
    )
    @GetMapping(value = "OCPP_JSON_STATUS")
    @ResponseBody
    public List<OcppJsonStatus> getOcppJsonStatus() {
        return chargePointHelperService.getOcppJsonStatus();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Boolean isFilterd(ConnectorStatusForm queryParams) {
        if (!isNull(queryParams)) {
            if (!isNull(queryParams.getChargeBoxId())) {
                if (!queryParams.getChargeBoxId().isBlank()) {
                    return true;
                }
            }
            if (!isNull(queryParams.getStatus())) {
                if (!queryParams.getStatus().isBlank()) {
                    return true;
                }
            }
        }
        return false;
    }
}

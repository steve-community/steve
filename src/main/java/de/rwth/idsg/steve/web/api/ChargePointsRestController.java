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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.ChargePointConnectionResponse;
import de.rwth.idsg.steve.web.dto.ChargePointConnectorResponse;
import de.rwth.idsg.steve.web.dto.ChargePointFormForCreate;
import de.rwth.idsg.steve.web.dto.ChargePointFormForUpdate;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

/**
 * @author huangkongsheng
 * @since 19.10.2026
 */
@Tag(name = "charge-point-controller",
    description = """
        Operations related to managing charge points and inspecting their connection state.
        The representation never contains credentials, authorization keys, certificates, or
        private key material.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/chargeBoxes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChargePointsRestController {

    private final ChargePointService chargePointService;

    @Operation(description = """
        Returns a list of charge points based on the query parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<ChargePoint.Overview> get(@ParameterObject ChargePointQueryForm params) {
        log.debug("Read request for query: {}", params);

        var response = chargePointService.getOverview(params, ChargePointQueryForm.ChargeBoxIdMatchType.Exact);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single charge point based on the chargeBoxPk.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{chargeBoxPk}")
    public ChargePoint.Overview getOne(@PathVariable("chargeBoxPk") Integer chargeBoxPk) {
        log.debug("Read request for chargeBoxPk: {}", chargeBoxPk);

        var response = getOneInternal(chargeBoxPk);
        log.debug("Read response: {}", response);
        return response;
    }

    @Operation(description = """
        Creates a new charge point with the provided parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChargePoint.Overview create(@RequestBody @Valid ChargePointFormForCreate params) {
        log.debug("Create request: {}", params);

        int chargeBoxPk = chargePointService.addChargePoint(params);

        var response = getOneInternal(chargeBoxPk);
        log.debug("Create response: {}", response);
        return response;
    }

    @Operation(description = """
        Updates an existing charge point with the provided parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PutMapping("/{chargeBoxPk}")
    public ChargePoint.Overview update(@PathVariable("chargeBoxPk") Integer chargeBoxPk,
                                       @RequestBody @Valid ChargePointFormForUpdate params) {
        params.setChargeBoxPk(chargeBoxPk); // the one from incoming params does not matter
        log.debug("Update request: {}", params);

        chargePointService.updateChargePoint(params);

        var response = getOneInternal(chargeBoxPk);
        log.debug("Update response: {}", response);
        return response;
    }

    @Operation(description = """
        Deletes an existing charge point based on the chargeBoxPk.
        Returns the deleted charge point.
        This mirrors the behaviour of the manager UI: deleting a charge point removes its
        configuration and history, regardless of whether it is currently connected.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @DeleteMapping("/{chargeBoxPk}")
    public ChargePoint.Overview delete(@PathVariable("chargeBoxPk") Integer chargeBoxPk) {
        log.debug("Delete request for chargeBoxPk: {}", chargeBoxPk);

        var response = getOneInternal(chargeBoxPk);
        chargePointService.deleteChargePoint(chargeBoxPk);

        log.debug("Delete response: {}", response);
        return response;
    }

    @Operation(description = """
        Returns the last known OCPP status of every connector of the charge point,
        together with the current transport-layer connection state.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{chargeBoxPk}/connectors")
    public List<ChargePointConnectorResponse> getConnectors(@PathVariable("chargeBoxPk") Integer chargeBoxPk) {
        log.debug("Read request for connectors of chargeBoxPk: {}", chargeBoxPk);

        var response = getConnectorStatuses(chargeBoxPk).stream()
            .map(ChargePointConnectorResponse::of)
            .toList();
        log.debug("Read response: {}", response);
        return response;
    }

    @Operation(description = """
        Returns the current transport connection state of the charge point, aggregated
        over its connectors, together with the last heartbeat.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{chargeBoxPk}/connection")
    public ChargePointConnectionResponse getConnection(@PathVariable("chargeBoxPk") Integer chargeBoxPk) {
        log.debug("Read request for connection state of chargeBoxPk: {}", chargeBoxPk);

        var connectors = getConnectorStatuses(chargeBoxPk);
        if (connectors.isEmpty()) {
            throw new SteveException.NotFound("No connector status found for this charge point");
        }

        var response = ChargePointConnectionResponse.of(connectors.get(0), connectors);
        log.debug("Read response: {}", response);
        return response;
    }

    private ChargePoint.Overview getOneInternal(int chargeBoxPk) {
        ChargePointQueryForm params = new ChargePointQueryForm();
        params.setChargeBoxPk(chargeBoxPk);

        List<ChargePoint.Overview> results =
            chargePointService.getOverview(params, ChargePointQueryForm.ChargeBoxIdMatchType.Exact);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this charge point");
        }
        return results.get(0);
    }

    private List<ConnectorStatus> getConnectorStatuses(int chargeBoxPk) {
        String chargeBoxId = getOneInternal(chargeBoxPk).getChargeBoxId();

        ConnectorStatusForm form = new ConnectorStatusForm();
        form.setChargeBoxId(chargeBoxId);

        return chargePointService.getChargePointConnectorStatus(form).stream()
            .filter(s -> s.getChargeBoxPk() == chargeBoxPk)
            .toList();
    }
}

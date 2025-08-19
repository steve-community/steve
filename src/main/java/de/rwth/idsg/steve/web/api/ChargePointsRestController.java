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

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.service.ChargePointsService;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Tag(name = "charge-points", description = "Operations related to managing ChargePoints.")
@RestController
@RequestMapping(value = {"/api/v1/chargepoints", "/api/v1/chargeboxes"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChargePointsRestController {

    private final ChargePointsService chargePointsService;

    @Operation(description = "Returns a list of ChargePoints based on the query parameters.")
    @StandardApiResponses
    @GetMapping
    public List<ApiChargePoint> get(@ParameterObject ChargePointQueryForm params) {
        return chargePointsService.getOverview(params).stream().map(ChargePointsRestController::toDto).toList();
    }

    @Operation(description = "Returns a single ChargePoint based on the ChargePointPk.")
    @StandardApiResponses
    @GetMapping("/{chargePointPk}")
    public ApiChargePoint getOne(@PathVariable("chargePointPk") Integer chargePointPk) {
        return toDto(chargePointsService.getDetails(chargePointPk));
    }

    @Operation(description = "Creates a new ChargePoint with the provided parameters.")
    @StandardApiResponses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiChargePoint create(@RequestBody @Valid ChargePointForm params) {
        var chargepointPk = chargePointsService.addChargePoint(params);
        return toDto(chargePointsService.getDetails(chargepointPk));
    }

    @Operation(description = "Updates an existing ChargePoint with the provided parameters.")
    @StandardApiResponses
    @PutMapping("/{chargePointPk}")
    public ApiChargePoint update(@PathVariable("chargePointPk") Integer chargePointPk,
                                                     @RequestBody @Valid ChargePointForm params) {
        params.setChargeBoxPk(chargePointPk);
        chargePointsService.updateChargePoint(params);
        return toDto(chargePointsService.getDetails(chargePointPk));
    }

    @Operation(description = "Deletes an existing Chargepoint based on the ChargePointPk.")
    @StandardApiResponses
    @DeleteMapping("/{chargePointPk}")
    public ApiChargePoint delete(@PathVariable("chargePointPk") Integer chargePointPk) {
        var response = chargePointsService.getDetails(chargePointPk);
        chargePointsService.deleteChargePoint(chargePointPk);
        return toDto(response);
    }

    private static ApiChargePoint toDto(ChargePoint.Overview overview) {
        return new ApiChargePoint(overview.getChargeBoxPk(), overview.getChargeBoxId());
    }

    private static ApiChargePoint toDto(ChargePoint.Details details) {
        return new ApiChargePoint(details.getChargeBox().getChargeBoxPk(), details.getChargeBox().getChargeBoxId());
    }

    @Data
    public static class ApiChargePoint {
        private final Integer chargeBoxPk;
        private final String chargeBoxId;
    }
}

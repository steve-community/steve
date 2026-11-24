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
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
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
@Tag(name = "charging-profile-controller",
    description = """
        Operations related to managing persisted charging profiles.
        A charging profile can be assigned to a connector of a charge box via
        the SetChargingProfile operation. This resource API manages the
        persisted profiles themselves; it does not send any OCPP commands.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/chargingProfiles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChargingProfilesRestController {

    private final ChargingProfileRepository chargingProfileRepository;

    @Operation(description = """
        Returns a list of persisted charging profiles based on the query parameters.
        Besides the profile's own fields, the list can be filtered by assignment
        (chargeBoxId, connectorId) and by validity (active=true selects profiles
        whose validity period contains now). OCPP enums are accepted both as Java
        constant names ("TX_PROFILE") and as the OCPP values returned by this API
        ("TxProfile").
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<ChargingProfile.Overview> get(@ParameterObject ChargingProfileQueryForm params) {
        log.debug("Read request for query: {}", params);

        var response = chargingProfileRepository.getOverview(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single persisted charging profile based on the chargingProfilePk.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{chargingProfilePk}")
    public ChargingProfile.Overview getOne(@PathVariable("chargingProfilePk") Integer chargingProfilePk) {
        log.debug("Read request for chargingProfilePk: {}", chargingProfilePk);

        var response = getOneInternal(chargingProfilePk);
        log.debug("Read response: {}", response);
        return response;
    }

    @Operation(description = """
        Creates a new persisted charging profile with the provided parameters.
        The returned primary key can be used with the SetChargingProfile and
        RemoteStartTransaction operations.
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
    public ChargingProfile.Overview create(@RequestBody @Valid ChargingProfileForm params) {
        log.debug("Create request: {}", params);

        int chargingProfilePk = chargingProfileRepository.add(params);

        var response = getOneInternal(chargingProfilePk);
        log.debug("Create response: {}", response);
        return response;
    }

    @Operation(description = """
        Updates an existing persisted charging profile with the provided parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PutMapping("/{chargingProfilePk}")
    public ChargingProfile.Overview update(@PathVariable("chargingProfilePk") Integer chargingProfilePk,
                                           @RequestBody @Valid ChargingProfileForm params) {
        params.setChargingProfilePk(chargingProfilePk); // the one from incoming params does not matter
        log.debug("Update request: {}", params);

        chargingProfileRepository.update(params);

        var response = getOneInternal(chargingProfilePk);
        log.debug("Update response: {}", response);
        return response;
    }

    @Operation(description = """
        Deletes an existing persisted charging profile based on the chargingProfilePk.
        Returns the deleted charging profile.
        This mirrors the behaviour of the manager UI: a profile that is still assigned
        to a connector cannot be deleted (the repository rejects it), so clear the
        assignment first via ClearChargingProfile.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @DeleteMapping("/{chargingProfilePk}")
    public ChargingProfile.Overview delete(@PathVariable("chargingProfilePk") Integer chargingProfilePk) {
        log.debug("Delete request for chargingProfilePk: {}", chargingProfilePk);

        var response = getOneInternal(chargingProfilePk);
        chargingProfileRepository.delete(chargingProfilePk);

        log.debug("Delete response: {}", response);
        return response;
    }

    private ChargingProfile.Overview getOneInternal(int chargingProfilePk) {
        ChargingProfileQueryForm params = new ChargingProfileQueryForm();
        params.setChargingProfilePk(chargingProfilePk);

        List<ChargingProfile.Overview> results = chargingProfileRepository.getOverview(params);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this charging profile");
        }
        return results.get(0);
    }
}

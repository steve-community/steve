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

import de.rwth.idsg.steve.repository.dto.OcppTag.OcppTagOverview;
import de.rwth.idsg.steve.service.OcppTagsService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.api.exception.NotFoundException;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm.OcppTagQueryFormForApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import jakarta.validation.Valid;

import static org.springframework.http.ResponseEntity.*;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@Tag(
        name = "tags",
        description =
                """
        Operations related to managing Ocpp Tags.
        An Ocpp Tag is the identifier of the actor that interacts with the charge box.
        It can be used for authorization, but also to start and stop charging sessions.
        An RFID card is an example of an Ocpp Tag.
        """)
@RestController
@RequestMapping(value = "/api/v1/ocppTags", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OcppTagsRestController {

    private final OcppTagsService ocppTagsService;

    @Operation(
            description =
                    """
        Returns a list of Ocpp Tags based on the query parameters.
        The query parameters can be used to filter the Ocpp Tags.
        """)
    @StandardApiResponses
    @GetMapping(value = "")
    public List<OcppTagOverview> get(@ParameterObject OcppTagQueryFormForApi params) {
        return ocppTagsService.getOverview(params);
    }

    @Operation(description = """
        Returns a single Ocpp Tag based on the ocppTagPk.
        """)
    @StandardApiResponses
    @GetMapping("/{ocppTagPk}")
    public OcppTagOverview getOne(@PathVariable("ocppTagPk") Integer ocppTagPk) {
        return getOneInternal(ocppTagPk);
    }

    @Operation(
            description =
                    """
        Creates a new Ocpp Tag with the provided parameters.
        The request body should contain the necessary information.
        """)
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Created"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "422",
                        description = "Unprocessable Entity",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class))
                        })
            })
    @PostMapping
    public ResponseEntity<OcppTagOverview> create(@RequestBody @Valid OcppTagForm params) {
        var ocppTagPk = ocppTagsService.addOcppTag(params);
        var body = getOneInternal(ocppTagPk);

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{ocppTagPk}")
                .buildAndExpand(ocppTagPk)
                .toUri();
        return created(location).body(body);
    }

    @Operation(description = """
        Updates an existing Ocpp Tag with the provided parameters.
        """)
    @StandardApiResponses
    @PutMapping("/{ocppTagPk}")
    public OcppTagOverview update(
            @PathVariable("ocppTagPk") Integer ocppTagPk, @RequestBody @Valid OcppTagForm params) {
        params.setOcppTagPk(ocppTagPk); // the one from incoming params does not matter
        ocppTagsService.updateOcppTag(params);

        return getOneInternal(ocppTagPk);
    }

    @Operation(
            description =
                    """
        Deletes an existing Ocpp Tag based on the ocppTagPk.
        Returns the deleted Ocpp Tag.
        """)
    @StandardApiResponses
    @DeleteMapping("/{ocppTagPk}")
    public OcppTagOverview delete(@PathVariable("ocppTagPk") Integer ocppTagPk) {
        var response = getOneInternal(ocppTagPk);
        ocppTagsService.deleteOcppTag(ocppTagPk);

        return response;
    }

    private OcppTagOverview getOneInternal(int ocppTagPk) {
        var params = new OcppTagQueryFormForApi();
        params.setOcppTagPk(ocppTagPk);

        var results = ocppTagsService.getOverview(params);
        if (results.isEmpty()) {
            throw new NotFoundException("Could not find ocppTag with id " + ocppTagPk);
        }
        return results.get(0);
    }
}

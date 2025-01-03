/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@Tag(name = "ocpp-tag-controller",
    description = """
        Operations related to managing Ocpp Tags.
        An Ocpp Tag is the identifier of the actor that interacts with the charge box.
        It can be used for authorization, but also to start and stop charging sessions.
        An RFID card is an example of an Ocpp Tag.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/ocppTags", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OcppTagsRestController {

    private final OcppTagService ocppTagService;

    @Operation(description = """
        Returns a list of Ocpp Tags based on the query parameters.
        The query parameters can be used to filter the Ocpp Tags.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    @ResponseBody
    public List<OcppTag.Overview> get(OcppTagQueryForm.ForApi params) {
        log.debug("Read request for query: {}", params);

        var response = ocppTagService.getOverview(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single Ocpp Tag based on the ocppTagPk.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{ocppTagPk}")
    @ResponseBody
    public OcppTag.Overview getOne(@PathVariable("ocppTagPk") Integer ocppTagPk) {
        log.debug("Read request for ocppTagPk: {}", ocppTagPk);

        var response = getOneInternal(ocppTagPk);
        log.debug("Read response: {}", response);
        return response;
    }

    @Operation(description = """
        Creates a new Ocpp Tag with the provided parameters.
        The request body should contain the necessary information.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public OcppTag.Overview create(@RequestBody @Valid OcppTagForm params) {
        log.debug("Create request: {}", params);

        int ocppTagPk = ocppTagService.addOcppTag(params);

        var response = getOneInternal(ocppTagPk);
        log.debug("Create response: {}", response);
        return response;
    }

    @Operation(description = """
        Updates an existing Ocpp Tag with the provided parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PutMapping("/{ocppTagPk}")
    @ResponseBody
    public OcppTag.Overview update(@PathVariable("ocppTagPk") Integer ocppTagPk, @RequestBody @Valid OcppTagForm params) {
        params.setOcppTagPk(ocppTagPk); // the one from incoming params does not matter
        log.debug("Update request: {}", params);

        ocppTagService.updateOcppTag(params);

        var response = getOneInternal(ocppTagPk);
        log.debug("Update response: {}", response);
        return response;
    }

    @Operation(description = """
        Deletes an existing Ocpp Tag based on the ocppTagPk.
        Returns the deleted Ocpp Tag.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @DeleteMapping("/{ocppTagPk}")
    @ResponseBody
    public OcppTag.Overview delete(@PathVariable("ocppTagPk") Integer ocppTagPk) {
        log.debug("Delete request for ocppTagPk: {}", ocppTagPk);

        var response = getOneInternal(ocppTagPk);
        ocppTagService.deleteOcppTag(ocppTagPk);

        log.debug("Delete response: {}", response);
        return response;
    }

    private OcppTag.Overview getOneInternal(int ocppTagPk) {
        OcppTagQueryForm.ForApi params = new OcppTagQueryForm.ForApi();
        params.setOcppTagPk(ocppTagPk);

        List<OcppTag.Overview> results = ocppTagService.getOverview(params);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this ocppTag");
        }
        return results.get(0);
    }
}

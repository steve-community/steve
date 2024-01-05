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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.validation.Valid;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/ocppTags", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OcppTagsRestController {

    private final OcppTagService ocppTagService;

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiErrorResponse.class)}
    )
    @GetMapping(value = "")
    @ResponseBody
    public List<OcppTag.Overview> get(OcppTagQueryForm.ForApi params) {
        log.debug("Read request for query: {}", params);

        var response = ocppTagService.getOverview(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiErrorResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiErrorResponse.class)}
    )
    @GetMapping("/{ocppTagPk}")
    @ResponseBody
    public OcppTag.Overview getOne(@PathVariable("ocppTagPk") Integer ocppTagPk) {
        log.debug("Read request for ocppTagPk: {}", ocppTagPk);

        var response = getOneInternal(ocppTagPk);
        log.debug("Read response: {}", response);
        return response;
    }

    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiErrorResponse.class),
        @ApiResponse(code = 422, message = "Unprocessable Entity", response = ApiErrorResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiErrorResponse.class)}
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

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiErrorResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiErrorResponse.class)}
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

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ApiErrorResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = ApiErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ApiErrorResponse.class)}
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

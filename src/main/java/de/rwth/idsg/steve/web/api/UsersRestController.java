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
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.UserService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
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
@Tag(name = "user-controller",
    description = """
        Operations related to managing domain users.
        A domain user is the operator-managed person or organization that can be
        associated with OCPP tags and their charging transactions.
        A domain user is distinct from manager login accounts (web_user) and
        API credentials.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersRestController {

    private final UserService userService;

    @Operation(description = """
        Returns a list of domain users based on the query parameters.
        The query parameters can be used to filter the users.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<User.Overview> get(@ParameterObject UserQueryForm params) {
        log.debug("Read request for query: {}", params);

        var response = userService.getOverview(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single domain user based on the userPk.
        The response contains the OCPP tags associated with the user.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{userPk}")
    public User.Overview getOne(@PathVariable("userPk") Integer userPk) {
        log.debug("Read request for userPk: {}", userPk);

        var response = getOneInternal(userPk);
        log.debug("Read response: {}", response);
        return response;
    }

    @Operation(description = """
        Creates a new domain user with the provided parameters.
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
    @ResponseStatus(HttpStatus.CREATED)
    public User.Overview create(@RequestBody @Valid UserForm params) {
        log.debug("Create request: {}", params);

        int userPk = userService.add(params);

        var response = getOneInternal(userPk);
        log.debug("Create response: {}", response);
        return response;
    }

    @Operation(description = """
        Updates an existing domain user with the provided parameters.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PutMapping("/{userPk}")
    public User.Overview update(@PathVariable("userPk") Integer userPk, @RequestBody @Valid UserForm params) {
        params.setUserPk(userPk); // the one from incoming params does not matter
        log.debug("Update request: {}", params);

        userService.update(params);

        var response = getOneInternal(userPk);
        log.debug("Update response: {}", response);
        return response;
    }

    @Operation(description = """
        Deletes an existing domain user based on the userPk.
        Returns the deleted domain user.
        This mirrors the behaviour of the manager UI.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @DeleteMapping("/{userPk}")
    public User.Overview delete(@PathVariable("userPk") Integer userPk) {
        log.debug("Delete request for userPk: {}", userPk);

        var response = getOneInternal(userPk);
        userService.delete(userPk);

        log.debug("Delete response: {}", response);
        return response;
    }

    private User.Overview getOneInternal(int userPk) {
        UserQueryForm params = new UserQueryForm();
        params.setUserPk(userPk);

        List<User.Overview> results = userService.getOverview(params);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this user");
        }
        return results.get(0);
    }
}

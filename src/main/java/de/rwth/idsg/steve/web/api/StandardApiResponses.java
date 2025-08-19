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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiControllerAdvice.ApiErrorResponse.class))}),
    @ApiResponse(responseCode = "401", description = "Unauthorized",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiControllerAdvice.ApiErrorResponse.class))}),
    @ApiResponse(responseCode = "404", description = "Not Found",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiControllerAdvice.ApiErrorResponse.class))}),
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiControllerAdvice.ApiErrorResponse.class))})}
)
public @interface StandardApiResponses {
}

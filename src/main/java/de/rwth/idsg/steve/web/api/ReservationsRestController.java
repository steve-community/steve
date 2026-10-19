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
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only access to SteVe's persisted reservation resources.
 *
 * <p>This resource API is deliberately separate from the OCPP operation endpoints
 * ({@code /api/v1/operations/ReserveNow} and {@code /api/v1/operations/CancelReservation}):
 * it exposes the persisted reservation state without sending any OCPP commands. Mutation
 * endpoints with defined OCPP side effects (sending ReserveNow / CancelReservation) can be
 * built on top of this once their semantics are agreed upon.
 *
 * @author huangkongsheng
 * @since 19.10.2026
 */
@Tag(name = "reservation-controller",
    description = """
        Read-only operations for inspecting persisted reservations.
        A reservation links a charge box, a connector, and an OCPP tag, and can be
        consumed by a transaction. This resource API exposes persisted state only -
        it never sends OCPP commands. Use the ReserveNow / CancelReservation
        operation endpoints to interact with stations.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservationsRestController {

    private final ReservationRepository reservationRepository;

    @Operation(description = """
        Returns a list of persisted reservations based on the query parameters.
        The query parameters can be used to filter by reservation id, transaction id,
        status (active/expired/cancelled/consumed), and creation or expiry period.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<Reservation> get(@ParameterObject ReservationQueryForm params) {
        log.debug("Read request for query: {}", params);

        var response = reservationRepository.getReservations(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single persisted reservation based on the reservationPk.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{reservationPk}")
    public Reservation getOne(@PathVariable("reservationPk") Integer reservationPk) {
        log.debug("Read request for reservationPk: {}", reservationPk);

        var response = getOneInternal(reservationPk);
        log.debug("Read response: {}", response);
        return response;
    }

    private Reservation getOneInternal(int reservationPk) {
        ReservationQueryForm params = new ReservationQueryForm();
        params.setReservationId(List.of(reservationPk));

        List<Reservation> results = reservationRepository.getReservations(params);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this reservation");
        }
        return results.get(0);
    }
}

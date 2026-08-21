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
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.OcppOperationResponse;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.CancelReservationStatus;
import ocpp.cp._2015._10.ReservationStatus;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.08.2026
 */
@Tag(name = "reservation-controller",
    description = """
        Operations related to querying and managing reservations.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservationsRestController {

    private final ReservationRepository reservationRepository;
    private final OcppOperationsService operationsService;

    @Operation(description = """
        Returns a list of reservations based on the query parameters.
        The query parameters can be used to filter the reservations.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<Reservation> get(@Valid @ParameterObject ReservationQueryForm.ReservationQueryFormForApi params) {
        log.debug("Read request for query: {}", params);

        var response = reservationRepository.getReservations(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns a single reservation based on the reservationPk.
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

    @Operation(description = """
        Triggers OCPP ReserveNow operation at the charge point.
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/reserve-now")
    public OcppOperationResponse<ReservationStatus> reserveNow(@RequestBody @Valid ReserveNowParams params) throws Exception {
        var callback = operationsService.reserveNow(params);
        return OcppOperationResponse.from(callback);
    }

    @Operation(description = """
        Triggers OCPP CancelReservation operation at the charge point.
        Only 1 charge point can be selected.
        """)
    @PostMapping(value = "/cancel")
    public OcppOperationResponse<CancelReservationStatus> cancelReservation(@RequestBody @Valid CancelReservationParams params) throws Exception {
        var callback = operationsService.cancelReservation(params);
        return OcppOperationResponse.from(callback);
    }

    private Reservation getOneInternal(int reservationPk) {
        ReservationQueryForm.ReservationQueryFormForApi params = new ReservationQueryForm.ReservationQueryFormForApi();
        params.setReservationId(List.of(reservationPk));
        params.setPeriodType(ReservationQueryForm.QueryPeriodType.ALL);

        List<Reservation> results = reservationRepository.getReservations(params);
        if (results.isEmpty()) {
            throw new SteveException.NotFound("Could not find this reservation");
        }
        return results.get(0);
    }
}

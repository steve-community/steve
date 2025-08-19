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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.service.ReservationsService;
import de.rwth.idsg.steve.web.api.exception.NotFoundException;
import de.rwth.idsg.steve.web.dto.ReservationForm;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "reservations")
@RestController
@RequestMapping(path = "/api/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservationsRestController {

    private final ReservationsService reservationsService;

    @GetMapping
    public List<Reservation> getReservations(ReservationQueryForm form) {
        return reservationsService.getReservations(form);
    }

    @PostMapping
    public Reservation addReservation(@Valid @RequestBody ReservationForm form) {
        var id = reservationsService.addReservation(form);
        return reservationsService.getReservation(id).orElseThrow(
            () -> new SteveException("Reservation not found after creation, this should never happen")
        );
    }

    @DeleteMapping("/{id}")
    public Reservation deleteReservation(@PathVariable int id) {
        var reservation = reservationsService.getReservation(id).orElseThrow(
            () -> new NotFoundException(String.format("Reservation with id %d not found", id))
        );
        reservationsService.deleteReservation(id);
        return reservation;
    }
}

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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.web.dto.ReservationForm;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationsService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> getReservations(ReservationQueryForm form) {
        return reservationRepository.getReservations(form);
    }

    public int addReservation(ReservationForm form) {
        var params = InsertReservationParams.builder()
                .idTag(form.getIdTag())
                .chargeBoxId(form.getChargeBoxId())
                .connectorId(form.getConnectorId())
                .startTimestamp(form.getStartTimestamp())
                .expiryTimestamp(form.getExpiryTimestamp())
                .build();
        return reservationRepository.insert(params);
    }

    public void deleteReservation(int reservationId) {
        reservationRepository.delete(reservationId);
    }

    public Optional<Reservation> getReservation(int id) {
        return reservationRepository.getReservation(id);
    }

    public List<Integer> getActiveReservationIds(String chargeBoxId) {
        return reservationRepository.getActiveReservationIds(chargeBoxId);
    }
}

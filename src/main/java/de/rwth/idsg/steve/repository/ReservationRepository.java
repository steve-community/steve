/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import org.jooq.Record1;
import org.jooq.Select;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
public interface ReservationRepository {
    List<Reservation> getReservations(ReservationQueryForm form);

    List<Integer> getActiveReservationIds(String chargeBoxId);

    /**
     * Returns the id of the reservation, if the reservation is inserted.
     */
    int insert(InsertReservationParams params);

    /**
     * Deletes the temporarily inserted reservation, when
     * 1) the charging station does not accept the reservation,
     * 2) there is a technical problem (communication failure etc.) with the charging station,
     */
    void delete(int reservationId);

    void accepted(int reservationId);
    void cancelled(int reservationId);
    void used(Select<Record1<Integer>> connectorPkSelect, String ocppIdTag, int reservationId, int transactionId);
}

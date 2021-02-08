/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import net.parkl.ocpp.entities.TransactionStart;

import java.util.List;

public interface ReservationService {
	void accepted(int reservationId);

	void delete(int reservationId);

	void cancelled(int reservationId);

	int insert(InsertReservationParams res);

	List<Integer> getActiveReservationIds(String chargeBoxId);

	List<de.rwth.idsg.steve.repository.dto.Reservation> getReservations(ReservationQueryForm form);

	void markReservationAsUsed(TransactionStart t, int reservationId, String chargeBoxId);
}

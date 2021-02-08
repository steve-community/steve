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
package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppReservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OcppReservationRepository extends CrudRepository<OcppReservation, Integer>{

	@Query("SELECT r.reservationPk FROM OcppReservation AS r WHERE r.connector.chargeBoxId=?1 AND r.expiryDatetime>?2 AND r.status='ACCEPTED'")
	List<Integer> findActiveReservationIds(String chargeBoxId,Date now);

	@Query("SELECT COUNT(r.reservationPk) FROM OcppReservation AS r WHERE r.expiryDatetime>?1 AND r.status='Accepted'")
	long countByExpiryDateGreater(Date now);

}

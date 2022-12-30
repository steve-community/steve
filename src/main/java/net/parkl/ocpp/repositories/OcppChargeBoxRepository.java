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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OcppChargeBoxRepository extends JpaRepository<OcppChargeBox, Integer> {

	OcppChargeBox findByChargeBoxId(String chargeBoxId);

	@Query("SELECT OBJECT(c) FROM OcppChargeBox AS c WHERE c.ocppProtocol=?1 AND c.endpointAddress IS NOT NULL AND c.registrationStatus IN ?2")
	List<OcppChargeBox> findByOcppProtocolAndRegistrationStatuses(String protocol, List<String> statuses);

	List<OcppChargeBox> findByChargeBoxIdIn(List<String> chargeBoxIdList);

	@Query("SELECT c.chargeBoxId FROM OcppChargeBox AS c")
	List<String> findAllChargeBoxIds();

	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp>=?1")
	long countLastHeartBeatAfter(Date date);
	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp>=?1 AND c.lastHeartbeatTimestamp<?2")
	long countLastHeartBeatBetween(Date date1,Date date2);

	@Query("SELECT COUNT(c.lastHeartbeatTimestamp) FROM OcppChargeBox AS c WHERE c.lastHeartbeatTimestamp<?1")
	long countLastHeartBeatBefore(Date date);
	
	@Query("SELECT c.registrationStatus FROM OcppChargeBox AS c WHERE c.chargeBoxId=?1")
    String findChargeBoxRegistrationStatus(String chargeBoxId);

	@Modifying(clearAutomatically=true)
	@Query("UPDATE OcppChargeBox c SET c.lastHeartbeatTimestamp=?2 WHERE c.chargeBoxId=?1")
	int updateChargeBoxLastHeartbeat(String chargeBoxId,Date lastHearbeat);

	@Modifying(clearAutomatically=true)
	@Query("UPDATE OcppChargeBox c SET c.ocppProtocol=?2 WHERE c.chargeBoxId=?1")
    void updateOcppProtocol(String chargeBoxId, String protocol);
}

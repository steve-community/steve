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

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorChargingProfile;
import net.parkl.ocpp.entities.ConnectorChargingProfileId;
import net.parkl.ocpp.entities.OcppChargingProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectorChargingProfileRepository extends CrudRepository<ConnectorChargingProfile, ConnectorChargingProfileId> {
    @Modifying
    @Query("DELETE FROM ConnectorChargingProfile AS ccp WHERE ccp.chargingProfile=?1 AND ccp.connector IN ?2")
    void deleteByChargingProfileAndConnectors(OcppChargingProfile profile, List<Connector> connectors);

    @Modifying
    @Query("DELETE FROM ConnectorChargingProfile AS ccp WHERE ccp.connector IN ?2")
    void deleteByConnectors(List<Connector> connectors);

    @Modifying
    @Query("DELETE FROM ConnectorChargingProfile AS ccp WHERE ccp.chargingProfile IN ?1 AND ccp.connector IN ?2")
    void deleteByChargingProfilesAndConnectors(List<OcppChargingProfile> profiles, List<Connector> connectors);

    @EntityGraph(attributePaths = {"connector", "connector.chargeBox", "chargingProfile"})
    @Query("SELECT OBJECT(ccp) FROM ConnectorChargingProfile AS ccp WHERE (?1 IS NULL OR ccp.connector.chargeBoxId=?1) AND (?2 IS NULL OR ccp.chargingProfile.chargingProfilePk=?2) AND (?3 IS NULL OR ccp.chargingProfile.description LIKE ?3) ORDER BY ccp.connector.chargeBoxId, ccp.connector.connectorId, ccp.chargingProfile.chargingProfilePk")
    List<ConnectorChargingProfile> search(String chargeBoxId, Integer chargingProfilePk, String chargingProfileDescription);

    long countByChargingProfile(OcppChargingProfile profile);

    @Query("SELECT ccp.connector.chargeBoxId FROM ConnectorChargingProfile AS ccp WHERE ccp.chargingProfile=?1")
    List<String> findChargeBoxIdsByChargingProfile(OcppChargingProfile profile);
}

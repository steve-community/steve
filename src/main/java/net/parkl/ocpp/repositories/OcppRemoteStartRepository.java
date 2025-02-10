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
import net.parkl.ocpp.entities.OcppRemoteStart;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OcppRemoteStartRepository extends CrudRepository<OcppRemoteStart, Integer>{
    @Modifying
    void deleteByConnectorAndOcppTag(Connector c, String idTag);

    @Query("select count(r) from OcppRemoteStart r where r.connector = ?1 and r.ocppTag = ?2 and r.createDate > ?3")
    long countByConnectorAndOcppTagAfter(Connector c, String idTag, LocalDateTime date);

    @Query("select count(r) from OcppRemoteStart r where r.connector in ?1 and r.ocppTag = ?2 and r.createDate > ?3")
    long countByConnectorsAndOcppTagAfter(List<Connector> connectors, String idTag, LocalDateTime date);

    @Modifying
    void deleteByConnectorIn(List<@NotNull Connector> connectors);
}

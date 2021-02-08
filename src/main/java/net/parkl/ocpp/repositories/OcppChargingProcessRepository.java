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
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OcppChargingProcessRepository extends CrudRepository<OcppChargingProcess, String>{

	OcppChargingProcess findByConnectorAndTransactionStartIsNullAndEndDateIsNull(Connector c);

	OcppChargingProcess findByTransactionStart(TransactionStart t);

	@Query("SELECT OBJECT(p) FROM OcppChargingProcess AS p WHERE p.connector.chargeBoxId=?1 AND p.transactionStart IS NOT NULL AND p.endDate IS NULL")
	List<OcppChargingProcess> findActiveByChargeBoxId(String chargeBoxId);

	OcppChargingProcess findByConnectorAndEndDateIsNull(Connector c);

	List<OcppChargingProcess> findAllByTransactionStartIsNullAndEndDateIsNull();

	@EntityGraph(attributePaths = {"transaction"})
	List<OcppChargingProcess> findAllByTransactionStartIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();

	@EntityGraph(attributePaths = {"transaction"})
	List<OcppChargingProcess> findAllByTransactionStartIsNotNullAndLimitMinuteIsNotNullAndEndDateIsNull();

	OcppChargingProcess findByOcppTagAndConnectorAndEndDateIsNull(String rfidTag, Connector connector);

	OcppChargingProcess findByOcppTagAndConnectorAndEndDateIsNullAndTransactionStartIsNotNull(String rfidTag, Connector connector);

	@Query("SELECT OBJECT(p) FROM OcppChargingProcess AS p WHERE p.transactionStart.transactionPk=?1")
    OcppChargingProcess findByTransactionId(int transactionId);


}

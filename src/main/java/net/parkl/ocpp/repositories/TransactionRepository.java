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

import net.parkl.ocpp.entities.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer>{

	@Query("SELECT t.transactionPk FROM Transaction AS t WHERE t.connector.chargeBoxId=?1 AND t.stopTimestamp IS NULL")
	List<Integer> findActiveTransactionIds(String chargeBoxId);

	long countByStopTimestampIsNull();

	@Query("SELECT t.transactionPk FROM Transaction AS t WHERE t.connector.chargeBoxId=?1 AND t.connector.connectorId=?2 AND t.stopTimestamp IS NULL")
	Integer findActiveTransactionId(String chargeBoxId,int connectorId);

	@Query("SELECT t.connector.chargeBoxId FROM Transaction AS t WHERE t.ocppTag=?1 AND t.stopTimestamp IS NULL AND t.stopValue IS NULL")
	List<String> findActiveChargeBoxIdsByOcppTag(String idTag);

	@Query("SELECT OBJECT(t) FROM Transaction AS t WHERE t.connector.chargeBoxId=?1 AND t.stopTimestamp IS NULL")
	List<Transaction> findActiveByChargeBox(String chargeBoxId);


	@Query("SELECT t.ocppTag, COUNT(t.ocppTag) FROM Transaction AS t WHERE t.stopTimestamp IS NULL " +
			"      AND t.stopValue IS NULL " +
			"      GROUP BY t.ocppTag")
	List<Object[]> findIdTagsInTransaction();

	@Query("SELECT COUNT(t.ocppTag) FROM Transaction AS t WHERE t.stopTimestamp IS NULL " +
			"      AND t.stopValue IS NULL AND t.ocppTag=?1")
	long countActiveTransactionsByIdTag(String idTag);

	@Query("SELECT OBJECT(t) FROM Transaction AS t WHERE t.stopTimestamp IS NULL AND t.startEventTimestamp<?1 ORDER BY t.startEventTimestamp ASC")
    List<Transaction> findActiveStartedBefore(Date threshold);
}

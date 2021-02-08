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

import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ConnectorMeterValueRepository extends CrudRepository<ConnectorMeterValue, Integer>{

	List<ConnectorMeterValue> findByTransaction(Transaction transaction);

	@Query("SELECT OBJECT(v) FROM ConnectorMeterValue AS v WHERE v.connector.chargeBoxId=?1 AND v.connector.connectorId=?2 AND v.valueTimestamp>?3")
	List<ConnectorMeterValue> findByChargeBoxIdAndConnectorIdAfter(String chargeBoxId, int connectorId,
			Date startTimestamp);
	@Query("SELECT OBJECT(v) FROM ConnectorMeterValue AS v WHERE v.connector.chargeBoxId=?1 AND v.connector.connectorId=?2 AND v.valueTimestamp>?3 AND v.valueTimestamp<?4")
	List<ConnectorMeterValue> findByChargeBoxIdAndConnectorIdBetween(String chargeBoxId, int connectorId,
			Date startTimestamp,Date stopTimestamp);

	List<ConnectorMeterValue> findByTransactionOrderByValueTimestampDesc(TransactionStart transaction);

	List<ConnectorMeterValue> findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(TransactionStart transaction, String measurand);

List<ConnectorMeterValue> findByTransactionAndMeasurandOrderByValueTimestampDesc(TransactionStart transaction, String measurand);

@Query("SELECT OBJECT(v) FROM ConnectorMeterValue AS v WHERE v.transaction.transactionPk=?1")
	List<ConnectorMeterValue> findByTransactionPk(int transactionPk);
}

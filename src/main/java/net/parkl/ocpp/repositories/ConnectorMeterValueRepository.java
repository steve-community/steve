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
import net.parkl.ocpp.entities.TransactionStart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ConnectorMeterValueRepository extends CrudRepository<ConnectorMeterValue, Integer>{


	@Query("SELECT v.valueTimestamp, v.value, v.readingContext, v.format, v.measurand, v.location, v.unit, v.phase " +
			"FROM ConnectorMeterValue AS v WHERE v.connector.chargeBoxId=?1 AND v.connector.connectorId=?2 AND v.valueTimestamp>?3")
	List<Object[]> findByChargeBoxIdAndConnectorIdAfter(String chargeBoxId, int connectorId,
			Date startTimestamp);
	@Query("SELECT v.valueTimestamp, v.value, v.readingContext, v.format, v.measurand, v.location, v.unit, v.phase " +
			"FROM ConnectorMeterValue AS v WHERE v.connector.chargeBoxId=?1 AND v.connector.connectorId=?2 AND v.valueTimestamp>?3 AND v.valueTimestamp<?4")
	List<Object[]> findByChargeBoxIdAndConnectorIdBetween(String chargeBoxId, int connectorId,
			Date startTimestamp,Date stopTimestamp);


	@Query("SELECT v.unit, v.value FROM ConnectorMeterValue AS v WHERE v.transaction=?1 AND v.measurand=?2 AND v.phase IS NULL ORDER BY v.valueTimestamp DESC")
	List<Object[]> findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(TransactionStart transaction, String measurand);

	@Query("SELECT v.unit, v.value FROM ConnectorMeterValue AS v WHERE v.transaction=?1 AND v.measurand=?2 AND v.phase IS NULL ORDER BY v.valueTimestamp DESC")
	List<Object[]> findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDescPage(TransactionStart transaction, String measurand, Pageable pageable);


	@Query("SELECT v.valueTimestamp, v.value, v.readingContext, v.format, v.measurand, v.location, v.unit, v.phase " +
			"FROM ConnectorMeterValue AS v WHERE v.transaction.transactionPk=?1")
	List<Object[]> findByTransactionPk(int transactionPk);

	@Query("SELECT " +
			"v.valueTimestamp, " +
			"MAX(CASE WHEN COALESCE(v.measurand, 'Energy.Active.Import.Register') = 'Energy.Active.Import.Register' THEN v.value END), " +
			"MAX(CASE WHEN v.measurand = 'Power.Active.Import' THEN v.value END), " +
			"MAX(CASE WHEN COALESCE(v.measurand, 'Energy.Active.Import.Register') = 'Energy.Active.Import.Register' THEN COALESCE(v.unit, 'wh') END), " +
			"MAX(CASE WHEN v.measurand = 'Power.Active.Import' THEN v.unit END), " +
			"MAX(CASE WHEN v.measurand = 'SoC' THEN v.value END) " +
			"FROM ConnectorMeterValue v " +
			"WHERE v.transaction=?1 " +
			"	AND (v.measurand IN ('Energy.Active.Import.Register', 'Power.Active.Import', 'SoC') OR v.measurand IS NULL) " +
			"	AND v.phase IS NULL " +
			"GROUP BY v.valueTimestamp " +
			"ORDER BY v.valueTimestamp ")
	List<Object[]> findEnergyAndPowerDataForTransaction(TransactionStart transactionStart);

}

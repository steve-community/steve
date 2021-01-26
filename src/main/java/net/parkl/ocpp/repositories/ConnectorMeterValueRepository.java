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

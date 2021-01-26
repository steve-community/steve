package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OcppChargingProcessRepository extends CrudRepository<OcppChargingProcess, String>{

	OcppChargingProcess findByConnectorAndTransactionIsNullAndEndDateIsNull(Connector c);

	OcppChargingProcess findByTransaction(TransactionStart t);

	@Query("SELECT OBJECT(p) FROM OcppChargingProcess AS p WHERE p.connector.chargeBoxId=?1 AND p.transaction IS NOT NULL AND p.endDate IS NULL")
	List<OcppChargingProcess> findActiveByChargeBoxId(String chargeBoxId);

	OcppChargingProcess findByConnectorAndEndDateIsNull(Connector c);

	List<OcppChargingProcess> findAllByTransactionIsNullAndEndDateIsNull();

	@EntityGraph(attributePaths = {"transaction"})
	List<OcppChargingProcess> findAllByTransactionIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();

	@EntityGraph(attributePaths = {"transaction"})
	List<OcppChargingProcess> findAllByTransactionIsNotNullAndLimitMinuteIsNotNullAndEndDateIsNull();

	OcppChargingProcess findByOcppTagAndConnectorAndEndDateIsNull(String rfidTag, Connector connector);

	@Query("SELECT OBJECT(p) FROM OcppChargingProcess AS p WHERE p.transaction.transactionPk=?1")
    OcppChargingProcess findByTransactionId(int transactionId);


}

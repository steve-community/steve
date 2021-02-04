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

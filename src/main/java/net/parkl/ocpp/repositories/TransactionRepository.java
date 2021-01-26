package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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


	@Query("SELECT t.ocppTag, COUNT(t.ocppTag) FROM Transaction AS t WHERE t.stopTimestamp IS NULL\n" +
			"      AND t.stopValue IS NULL\n" +
			"      GROUP BY t.ocppTag")
	List<Object[]> findIdTagsInTransaction();

	@Query("SELECT COUNT(t.ocppTag) FROM Transaction AS t WHERE t.stopTimestamp IS NULL\n" +
			"      AND t.stopValue IS NULL AND t.ocppTag=?1")
	long countActiveTransactionsByIdTag(String idTag);
}

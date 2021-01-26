package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.TransactionStart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface TransactionStartRepository extends CrudRepository<TransactionStart, Integer> {
    @Query("SELECT OBJECT(t) FROM TransactionStart AS t WHERE t.connector.chargeBoxId=?1 AND t.connector.connectorId=?2 AND t.startTimestamp>?3 ORDER BY t.startTimestamp")
    List<TransactionStart> findNextTransactions(String chargeBoxId, int connectorId, Date startTimestamp, Pageable pageable);

    @Query("SELECT OBJECT(t) FROM TransactionStart AS t WHERE t.connector=?1 AND t.ocppTag=?2 AND t.startTimestamp=?3 AND t.startValue=?4")
    TransactionStart findByConnectorAndIdTagAndStartValues(Connector c, String idTag, Date startDate, String startValue);
}

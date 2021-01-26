package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.TransactionStop;
import net.parkl.ocpp.entities.TransactionStopId;
import org.springframework.data.repository.CrudRepository;

public interface TransactionStopRepository extends CrudRepository<TransactionStop, TransactionStopId> {
}

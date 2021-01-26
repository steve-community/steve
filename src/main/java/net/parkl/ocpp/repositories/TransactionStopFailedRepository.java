package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.TransactionStopFailed;
import net.parkl.ocpp.entities.TransactionStopId;
import org.springframework.data.repository.CrudRepository;

public interface TransactionStopFailedRepository extends CrudRepository<TransactionStopFailed, TransactionStopId> {
}

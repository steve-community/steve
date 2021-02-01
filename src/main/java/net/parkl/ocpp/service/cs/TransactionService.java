package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;

import java.io.Writer;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

	List<Integer> getActiveTransactionIds(String chargeBoxId);
	List<de.rwth.idsg.steve.repository.dto.Transaction> getTransactions(TransactionQueryForm form);
	TransactionDetails getDetails(int transactionPk, boolean firstArrivingMeterValueIfMultiple);
	void writeTransactionsCSV(TransactionQueryForm form, Writer writer);
	List<String> getChargeBoxIdsOfActiveTransactions(String idTag);

    Optional<Transaction> findTransaction(int transactionPk);

	Optional<TransactionStart> findTransactionStart(int transactionPk);

	long getActiveTransactionCountByIdTag(String idTag);

	default TransactionDetails getDetails(int transactionPk) {
		return getDetails(transactionPk, true);
	}

	Integer insertTransaction(InsertTransactionParams params);

	void updateTransaction(UpdateTransactionParams params);

}

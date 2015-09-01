package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface TransactionRepository {
    List<Transaction> getTransactions(TransactionQueryForm form);
    String getTransactionsCSV(TransactionQueryForm form);
    List<Integer> getActiveTransactionIds(String chargeBoxId);
}

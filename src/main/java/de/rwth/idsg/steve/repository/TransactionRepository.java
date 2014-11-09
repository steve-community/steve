package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Transaction;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface TransactionRepository {
    List<Transaction> getTransactions();
    List<Integer> getActiveTransactionIds(String chargeBoxId);
}

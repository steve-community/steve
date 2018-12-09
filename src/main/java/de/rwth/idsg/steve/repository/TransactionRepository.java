package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;

import java.io.Writer;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface TransactionRepository {
    List<Transaction> getTransactions(TransactionQueryForm form);
    void writeTransactionsCSV(TransactionQueryForm form, Writer writer);
    List<Integer> getActiveTransactionIds(String chargeBoxId);
    TransactionDetails getDetails(int transactionPk, boolean firstArrivingMeterValueIfMultiple);

    default TransactionDetails getDetails(int transactionPk) {
        return getDetails(transactionPk, true);
    }

    /**
     * Why plural: A transaction in the db remains 'active' (stop value and stop timestamp are null) until a
     * StopTransaction is received. Are these values null because the charging process is still going on or the station
     * did not send a StopTransaction message since it has connectivity issues and is therefore offline? The latter
     * causes us problems, because a StopTransaction might arrive a long time after the charging process actually ends.
     * Therefore, we have to consider that multiple stations might have active transactions for the same ocppIdTag.
     */
    List<String> getChargeBoxIdsOfActiveTransactions(String ocppIdTag);
}

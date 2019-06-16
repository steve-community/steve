/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

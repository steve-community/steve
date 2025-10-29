/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getTransactions(TransactionQueryForm form) {
        return transactionRepository.getTransactions(form);
    }

    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        transactionRepository.writeTransactionsCSV(form, writer);
    }

    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return transactionRepository.getActiveTransactionIds(chargeBoxId);
    }

    public TransactionDetails getDetails(int transactionPk) {
        return transactionRepository.getDetails(transactionPk);
    }

    public Transaction getTransaction(int transactionPk) {
        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setReturnCSV(false);
        form.setType(TransactionQueryForm.QueryType.ALL);

        return transactionRepository.getTransactions(form).getFirst();
    }

    public Transaction getActiveTransaction(String chargeBoxId, Integer connectorId) {
        TransactionQueryForm form = new TransactionQueryForm();
        form.setChargeBoxId(chargeBoxId);
        form.setConnectorId(connectorId);
        form.setReturnCSV(false);
        form.setType(TransactionQueryForm.QueryType.ACTIVE);

        var transactions = transactionRepository.getTransactions(form);
        if (transactions.isEmpty()) {
            return null;
        } else if (transactions.size() == 1) {
            return transactions.get(0);
        } else {
            throw new IllegalStateException("There are multiple active transactions with the same charge box id and connector id");
        }
    }
}

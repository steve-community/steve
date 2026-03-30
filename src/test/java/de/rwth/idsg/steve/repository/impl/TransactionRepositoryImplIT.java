/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.StringWriter;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class TransactionRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getTransactions() {
        var result = assertNoDatabaseException(() -> repository.getTransactions(new TransactionQueryForm()));
        Assertions.assertNotNull(result);
    }

    @Test
    public void writeTransactionsCSV() {
        StringWriter writer = new StringWriter();
        assertNoDatabaseException(() -> repository.writeTransactionsCSV(new TransactionQueryForm(), writer));
        Assertions.assertNotNull(writer.toString());
    }

    @Test
    public void getActiveTransactionIds() {
        var ids = assertNoDatabaseException(() -> repository.getActiveTransactionIds(KNOWN_CHARGE_BOX_ID));
        Assertions.assertNotNull(ids);
    }

    @Test
    public void getDetails() {
        Integer connectorPk = dslContext.select(CONNECTOR.CONNECTOR_PK)
            .from(CONNECTOR)
            .where(CONNECTOR.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(CONNECTOR.CONNECTOR_ID.eq(1))
            .fetchOne(CONNECTOR.CONNECTOR_PK);

        Integer txId = dslContext.insertInto(TRANSACTION_START)
            .set(TRANSACTION_START.EVENT_TIMESTAMP, DateTime.now())
            .set(TRANSACTION_START.CONNECTOR_PK, connectorPk)
            .set(TRANSACTION_START.ID_TAG, KNOWN_OCPP_TAG)
            .set(TRANSACTION_START.START_TIMESTAMP, DateTime.now().minusMinutes(5))
            .set(TRANSACTION_START.START_VALUE, "100")
            .returning(TRANSACTION_START.TRANSACTION_PK)
            .fetchOne()
            .getTransactionPk();

        var details = assertNoDatabaseException(() -> repository.getDetails(txId));
        Assertions.assertNotNull(details);
        Assertions.assertEquals(txId, details.getTransaction().getId());
    }

    @Test
    public void getStoppedTransactions() {
        var result = assertNoDatabaseException(() -> repository.getStoppedTransactions(DateTime.now().minusDays(1), DateTime.now()));
        Assertions.assertNotNull(result);
    }
}

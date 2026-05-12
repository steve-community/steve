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
import jooq.steve.db.enums.EvseTopologySource;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.StringWriter;

import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.Evse.EVSE;
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
        Integer connectorPk = dslContext.select(EVSE.EVSE_PK)
            .from(EVSE)
            .where(EVSE.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(EVSE.EVSE_ID.eq(1))
            .fetchOne(EVSE.EVSE_PK);

        Integer txId = dslContext.insertInto(TRANSACTION_START)
            .set(TRANSACTION_START.EVENT_TIMESTAMP, DateTime.now())
            .set(TRANSACTION_START.EVSE_PK, connectorPk)
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
    public void getDetailsForZombieTransactionDoesNotIncludeNextTransactionStartValue() {
        Integer connectorPk = dslContext.select(EVSE.EVSE_PK)
            .from(EVSE)
            .where(EVSE.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(EVSE.EVSE_ID.eq(1))
            .fetchOne(EVSE.EVSE_PK);

        DateTime firstStart = DateTime.now().minusMinutes(20);
        DateTime nextStart = firstStart.plusMinutes(10);

        Integer firstTxId = insertTransactionStart(connectorPk, firstStart, "100");
        insertTransactionStart(connectorPk, nextStart, "200");

        dslContext.insertInto(CONNECTOR_METER_VALUE)
            .set(CONNECTOR_METER_VALUE.EVSE_PK, connectorPk)
            .set(CONNECTOR_METER_VALUE.VALUE_TIMESTAMP, firstStart.plusMinutes(5))
            .set(CONNECTOR_METER_VALUE.VALUE, "150")
            .set(CONNECTOR_METER_VALUE.UNIT, "Wh")
            .execute();

        dslContext.insertInto(CONNECTOR_METER_VALUE)
            .set(CONNECTOR_METER_VALUE.EVSE_PK, connectorPk)
            .set(CONNECTOR_METER_VALUE.VALUE_TIMESTAMP, nextStart)
            .set(CONNECTOR_METER_VALUE.VALUE, "200")
            .set(CONNECTOR_METER_VALUE.UNIT, "Wh")
            .execute();

        var details = assertNoDatabaseException(() -> repository.getDetails(firstTxId));

        Assertions.assertEquals(1, details.getValues().size());
        Assertions.assertEquals("150", details.getValues().getFirst().getValue());
        Assertions.assertEquals(nextStart, details.getNextTransactionStart().getStartTimestamp());
    }

    @Test
    public void getStoppedTransactions() {
        var result = assertNoDatabaseException(() -> repository.getStoppedTransactions(DateTime.now().minusDays(1), DateTime.now()));
        Assertions.assertNotNull(result);
    }

    private Integer insertTransactionStart(Integer connectorPk, DateTime startTimestamp, String startValue) {
        return dslContext.insertInto(TRANSACTION_START)
            .set(TRANSACTION_START.EVENT_TIMESTAMP, startTimestamp)
            .set(TRANSACTION_START.EVSE_PK, connectorPk)
            .set(TRANSACTION_START.ID_TAG, KNOWN_OCPP_TAG)
            .set(TRANSACTION_START.START_TIMESTAMP, startTimestamp)
            .set(TRANSACTION_START.START_VALUE, startValue)
            .returning(TRANSACTION_START.TRANSACTION_PK)
            .fetchOne()
            .getTransactionPk();
    }
}

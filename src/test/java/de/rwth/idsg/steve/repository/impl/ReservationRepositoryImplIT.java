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

import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.Record1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Reservation.RESERVATION;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class ReservationRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private ReservationRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getReservations() {
        var rows = assertNoDatabaseException(() -> repository.getReservations(new ReservationQueryForm()));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getActiveReservationIds() {
        var ids = assertNoDatabaseException(() -> repository.getActiveReservationIds(KNOWN_CHARGE_BOX_ID));
        Assertions.assertNotNull(ids);
    }

    @Test
    public void insert() {
        Integer id = assertNoDatabaseException(() -> repository.insert(insertReservationParams()));
        Assertions.assertNotNull(id);

        Integer count = dslContext.selectCount()
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void delete() {
        Integer id = repository.insert(insertReservationParams());
        assertNoDatabaseException(() -> repository.delete(id));

        Integer count = dslContext.selectCount()
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void accepted() {
        Integer id = repository.insert(insertReservationParams());
        assertNoDatabaseException(() -> repository.accepted(id));

        String status = dslContext.select(RESERVATION.STATUS)
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(RESERVATION.STATUS);
        Assertions.assertEquals("ACCEPTED", status);
    }

    @Test
    public void cancelled() {
        Integer id = repository.insert(insertReservationParams());
        assertNoDatabaseException(() -> repository.cancelled(id));

        String status = dslContext.select(RESERVATION.STATUS)
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(RESERVATION.STATUS);
        Assertions.assertEquals("CANCELLED", status);
    }

    @Test
    public void used() {
        useReservation(KNOWN_OCPP_TAG);
    }

    @Test
    public void usedByParentIdTag() {
        String parentIdTag = KNOWN_OCPP_TAG + "_parent";

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, parentIdTag)
            .onDuplicateKeyIgnore()
            .execute();

        dslContext.update(OCPP_TAG)
            .set(OCPP_TAG.PARENT_ID_TAG, parentIdTag)
            .where(OCPP_TAG.ID_TAG.eq(KNOWN_OCPP_TAG))
            .execute();

        useReservation(parentIdTag);
    }

    @Test
    public void cancelActiveReservations() {
        Integer id = repository.insert(insertReservationParams());
        repository.accepted(id);

        assertNoDatabaseException(() -> repository.cancelActiveReservations(KNOWN_CHARGE_BOX_ID, 1));

        String status = dslContext.select(RESERVATION.STATUS)
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(RESERVATION.STATUS);
        Assertions.assertEquals("CANCELLED", status);
    }

    private void useReservation(String idTagFromTransaction) {
        Integer id = repository.insert(insertReservationParams());
        repository.accepted(id);

        Integer connectorPk = dslContext.select(CONNECTOR.CONNECTOR_PK)
            .from(CONNECTOR)
            .where(CONNECTOR.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(CONNECTOR.CONNECTOR_ID.eq(1))
            .fetchOne(CONNECTOR.CONNECTOR_PK);
        Assertions.assertNotNull(connectorPk);

        Integer transactionPk = dslContext.insertInto(TRANSACTION_START)
            .set(TRANSACTION_START.CONNECTOR_PK, connectorPk)
            .set(TRANSACTION_START.ID_TAG, idTagFromTransaction)
            .set(TRANSACTION_START.EVENT_TIMESTAMP, DateTime.now())
            .set(TRANSACTION_START.START_TIMESTAMP, DateTime.now().minusMinutes(1))
            .set(TRANSACTION_START.START_VALUE, "100")
            .returning(TRANSACTION_START.TRANSACTION_PK)
            .fetchOne()
            .getTransactionPk();

        Select<Record1<Integer>> connectorPkSelect = dslContext.select(CONNECTOR.CONNECTOR_PK)
            .from(CONNECTOR)
            .where(CONNECTOR.CONNECTOR_PK.eq(connectorPk));
        assertNoDatabaseException(() -> repository.used(connectorPkSelect, idTagFromTransaction, id, transactionPk));

        String status = dslContext.select(RESERVATION.STATUS)
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(RESERVATION.STATUS);
        Assertions.assertEquals("USED", status);

        Integer linkedTransactionPk = dslContext.select(RESERVATION.TRANSACTION_PK)
            .from(RESERVATION)
            .where(RESERVATION.RESERVATION_PK.eq(id))
            .fetchOne(RESERVATION.TRANSACTION_PK);
        Assertions.assertEquals(transactionPk, linkedTransactionPk);
    }

    private static InsertReservationParams insertReservationParams() {
        return InsertReservationParams.builder()
            .chargeBoxId(KNOWN_CHARGE_BOX_ID)
            .connectorId(1)
            .idTag(KNOWN_OCPP_TAG)
            .startTimestamp(DateTime.now())
            .expiryTimestamp(DateTime.now().plusHours(1))
            .build();
    }
}

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.Connector.CONNECTOR;

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
        assertNoDatabaseException(() -> repository.getReservations(new ReservationQueryForm()));
    }

    @Test
    public void getActiveReservationIds() {
        assertNoDatabaseException(() -> repository.getActiveReservationIds(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void insert() {
        assertNoDatabaseException(() -> repository.insert(insertReservationParams()));
    }

    @Test
    public void delete() {
        assertNoDatabaseException(() -> repository.delete(1));
    }

    @Test
    public void accepted() {
        assertNoDatabaseException(() -> repository.accepted(1));
    }

    @Test
    public void cancelled() {
        assertNoDatabaseException(() -> repository.cancelled(1));
    }

    @Test
    public void used() {
        Select<Record1<Integer>> connectorPkSelect = dslContext.select(CONNECTOR.CONNECTOR_PK)
            .from(CONNECTOR)
            .where(CONNECTOR.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(CONNECTOR.CONNECTOR_ID.eq(1));
        assertNoDatabaseException(() -> repository.used(connectorPkSelect, KNOWN_OCPP_TAG, 1, 1));
    }

    @Test
    public void cancelActiveReservations() {
        assertNoDatabaseException(() -> repository.cancelActiveReservations(KNOWN_CHARGE_BOX_ID, 1));
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

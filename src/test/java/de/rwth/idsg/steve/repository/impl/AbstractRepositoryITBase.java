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

import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import jooq.steve.db.enums.EvseTopologySource;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.UUID;

import static jooq.steve.db.tables.Evse.EVSE;
import static jooq.steve.db.tables.EvseConnector.EVSE_CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;

/**
 * Created with assistance from GPT-5.3-Codex
 */
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
abstract class AbstractRepositoryITBase {

    protected static final String KNOWN_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    protected static final String KNOWN_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    @FunctionalInterface
    protected interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    protected interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    protected static void assertNoDatabaseException(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (containsDatabaseException(t)) {
                Assertions.fail("Database exception", t);
            }
            Assertions.fail("Unexpected exception", t);
        }
    }

    protected static <T> T assertNoDatabaseException(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            if (containsDatabaseException(t)) {
                Assertions.fail("Database exception", t);
            }
            return Assertions.fail("Unexpected exception", t);
        }
    }

    private static boolean containsDatabaseException(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException || t instanceof DataAccessException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    protected static void assertAuditTimestampsAreSet(DateTime createdAt, DateTime updatedAt) {
        Assertions.assertNotNull(createdAt);
        Assertions.assertNotNull(updatedAt);
    }

    protected static void assertAuditTimestampsAfterUpdate(DateTime createdAtBefore,
                                                           DateTime updatedAtBefore,
                                                           DateTime createdAtAfter,
                                                           DateTime updatedAtAfter) {
        Assertions.assertEquals(createdAtBefore, createdAtAfter);
        Assertions.assertTrue(updatedAtAfter.isAfter(updatedAtBefore),
            () -> "Expected updated_at to advance from " + updatedAtBefore + " to " + updatedAtAfter);
    }

    protected static void waitForTimestampTick() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assertions.fail("Interrupted while waiting for timestamp tick", e);
        }
    }

    protected static void resetDatabase(DSLContext dslContext) {
        var preparer = new __DatabasePreparer__(dslContext);
        preparer.prepare();

        dslContext.insertInto(EVSE)
            .set(EVSE.CHARGE_BOX_ID, KNOWN_CHARGE_BOX_ID)
            .set(EVSE.TOPOLOGY_SOURCE, EvseTopologySource.ocpp1)
            .set(EVSE.EVSE_ID, 1)
            .onDuplicateKeyIgnore()
            .execute();

        Integer evsePk = dslContext.select(EVSE.EVSE_PK)
            .from(EVSE)
            .where(EVSE.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(EVSE.EVSE_ID.eq(1))
            .fetchOne(EVSE.EVSE_PK);

        dslContext.insertInto(EVSE_CONNECTOR)
            .set(EVSE_CONNECTOR.EVSE_PK, evsePk)
            .set(EVSE_CONNECTOR.CONNECTOR_ID, 1)
            .onDuplicateKeyIgnore()
            .execute();

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, KNOWN_OCPP_TAG)
            .onDuplicateKeyIgnore()
            .execute();
    }

    protected static String uniqueId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}

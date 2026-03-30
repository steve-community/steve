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
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Assertions;

import java.sql.SQLException;
import java.util.UUID;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;

abstract class AbstractRepositoryITBase {

    protected static final String KNOWN_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    protected static final String KNOWN_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    @FunctionalInterface
    protected interface ThrowingRunnable {
        void run() throws Exception;
    }

    protected static void assertNoDatabaseException(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (containsDatabaseException(t)) {
                Assertions.fail("Database exception", t);
            }
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

    protected static void resetDatabase(DSLContext dslContext) {
        var preparer = new __DatabasePreparer__(dslContext);
        preparer.cleanUp();
        preparer.prepare();

        dslContext.insertInto(CONNECTOR)
            .set(CONNECTOR.CHARGE_BOX_ID, KNOWN_CHARGE_BOX_ID)
            .set(CONNECTOR.CONNECTOR_ID, 1)
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

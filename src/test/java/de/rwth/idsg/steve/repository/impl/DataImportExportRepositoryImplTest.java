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

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataImportExportRepositoryImplTest {

    private final ImportTable table = new ImportTable();
    private final List<MockExecuteContext> statements = new ArrayList<>();
    private DataImportExportRepositoryImpl repository;

    @BeforeEach
    void setup() {
        DSLContext ctx = DSL.using(new MockConnection(execution -> {
            statements.add(execution);
            return new MockResult[] {new MockResult(1)};
        }), SQLDialect.MYSQL);
        repository = new DataImportExportRepositoryImpl(ctx);
    }

    @Test
    void importsReorderedColumnsWithJsonTimestampsAndNulls() {
        importCsv("""
            ocpp_configuration,last_heartbeat,security_profile
            "{""label"":""café, station""}",2026-09-28T19:02:57Z,0
            NULL,NULL,1
            """);

        var insert = statements.get(1);
        assertTrue(insert.sql().startsWith("insert into `charge_box` (`ocpp_configuration`, last_heartbeat, `security_profile`)"), insert.sql());
        assertArrayEquals(new Object[] {
            "{\"label\":\"café, station\"}", Timestamp.from(Instant.parse("2026-09-28T19:02:57Z")).toString(), 0,
            null, null, 1
        }, insert.bindings());
        assertEquals(2, statements.size());
    }

    @Test
    void importsColumnsInGeneratedOrder() {
        importCsv("""
            security_profile,ocpp_configuration,last_heartbeat
            0,{},2026-09-28T19:02:57Z
            """);

        var insert = statements.get(1);
        assertTrue(insert.sql().startsWith("insert into `charge_box` (`security_profile`, `ocpp_configuration`, last_heartbeat)"), insert.sql());
        assertArrayEquals(new Object[] {0, "{}", Timestamp.from(Instant.parse("2026-09-28T19:02:57Z")).toString()}, insert.bindings());
    }

    @Test
    void rejectsUnknownColumnsInsteadOfSilentlyDroppingTheirValues() {
        var error = assertThrows(IllegalArgumentException.class, () -> importCsv("""
            security_profile,unknown_column
            0,{}
            """));

        assertTrue(error.getMessage().contains("Unknown CSV column 'unknown_column' for table 'charge_box'"));
        assertEquals(1, statements.size());
        assertTrue(statements.getFirst().sql().startsWith("delete from"));
    }

    private void importCsv(String csv) {
        repository.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), table);
    }

    // Different types make a positional mismatch observable without a live database.
    private static class ImportTable extends TableImpl<Record> {
        final Field<Integer> securityProfile = createField(DSL.name("security_profile"), SQLDataType.INTEGER, this);
        final Field<JSON> ocppConfiguration = createField(DSL.name("ocpp_configuration"), SQLDataType.JSON, this);
        final Field<Timestamp> lastHeartbeat = createField(DSL.name("last_heartbeat"), SQLDataType.TIMESTAMP, this);

        ImportTable() {
            super(DSL.name("charge_box"));
        }
    }
}

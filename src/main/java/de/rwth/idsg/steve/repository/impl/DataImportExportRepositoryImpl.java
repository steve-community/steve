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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.DataImportExportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.CSVFormat;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.11.2025
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DataImportExportRepositoryImpl implements DataImportExportRepository {

    private static final int BATCH_SIZE = 1000;

    private final DSLContext ctx;

    private final CSVFormat csvFormatWithHeader = new CSVFormat();
    private final CSVFormat csvFormatNoHeader = csvFormatWithHeader.header(false);

    @Override
    public void exportCsv(Writer writer, Table<?> table) throws IOException {
        // write header line
        {
            // the csv has two lines: header line and another line for the empty record with empty cells
            String csv = ctx.newRecord(table).formatCSV(csvFormatWithHeader);
            // get only the first header line
            String header = csv.split(csvFormatWithHeader.newline())[0];
            writer.write(header);
            writer.write(csvFormatNoHeader.newline());
        }

        try (Cursor<?> cursor = ctx.selectFrom(table).fetchSize(BATCH_SIZE).fetchLazy()) {
            while (cursor.hasNext()) {
                var book = cursor.fetchNext();
                if (book != null) {
                    book.formatCSV(writer, csvFormatNoHeader);
                }
            }
        }
    }

    /**
     * https://www.jooq.org/doc/latest/manual/sql-execution/importing/importing-api/
     * https://www.jooq.org/doc/latest/manual/sql-execution/importing/importing-sources/importing-source-csv/
     * https://www.jooq.org/doc/latest/manual/sql-execution/importing/importing-options/importing-option-throttling/
     */
    @Override
    public void importCsv(InputStream in, Table<?> table) throws IOException {
        ctx.deleteFrom(table).execute();

        var loader = ctx.loadInto(table)
            .bulkAfter(BATCH_SIZE) // Put up to X rows in a single bulk statement.
            .batchAfter(BATCH_SIZE) // Put up to X statements (bulk or not) in a single statement batch.
            .loadCSV(in, StandardCharsets.UTF_8)
            .fieldsCorresponding()
            .execute();

        int processed = loader.processed();

        log.info("Imported '{}' with processedRows={}, storedRows={}, errorCount={}, errorMessages={}",
            table.getName(),
            loader.processed(),
            loader.stored(),
            loader.errors().size(),
            loader.errors().stream().map(it -> it.exception().getMessage()).toList()
        );

        // Update the sequence/auto-increment after bulk insert
        if (processed > 0) {
            resetAutoIncrement(table);
        }
    }

    /**
     * Reset the auto-increment/sequence for a table to the max ID value + 1
     */
    private void resetAutoIncrement(Table<?> table) {
        var primaryKey = table.getPrimaryKey();
        if (primaryKey == null) {
            return;
        }

        var primaryKeyFields = primaryKey.getFields();
        if (primaryKeyFields.isEmpty()) {
            return;
        } else if (primaryKeyFields.size() > 1) {
            log.warn("Found more than one PK for table {}", table.getName());
            return;
        }

        // Get the primary key field (assuming it's the auto-increment field)
        TableField<?, ?> pkField = primaryKeyFields.get(0);

        // Get the maximum ID value from the table
        Object maxId = ctx.select(DSL.max(pkField))
            .from(table)
            .fetchOne(0);

        if (maxId == null) {
            return; // Table is empty
        }

        if (!(maxId instanceof Number)) {
            log.debug("Nothing to auto-increment: PK '{}' for table '{}' is not a number, skipping", pkField.getName(), table.getName());
            return;
        }

        long nextVal = ((Number) maxId).longValue() + 1;

        SQLDialect dialectFamily = ctx.configuration().dialect().family();

        if (dialectFamily == SQLDialect.MYSQL || dialectFamily == SQLDialect.MARIADB) {
            ctx.execute("ALTER TABLE " + table.getName() + " AUTO_INCREMENT = " + nextVal);
        } else {
            log.warn("Auto increment not supported for dialect family {}", dialectFamily);
        }
    }

}

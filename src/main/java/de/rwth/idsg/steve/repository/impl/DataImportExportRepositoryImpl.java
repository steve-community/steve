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
import org.apache.commons.lang3.StringUtils;
import org.jooq.CSVFormat;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.LoaderError;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.AbstractConverter;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

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
    private final Converter<String, Timestamp> isoTimestampConverter = new IsoTimestampConverter();

    /**
     * DateTime will be exported via its toString method in the else-block of {@link org.jooq.impl.AbstractResult#format0(Object, boolean, boolean)}
     * because nothing else matches. The serialized values will be ISO8601 format.
     */
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
                var row = cursor.fetchNext();
                if (row != null) {
                    row.formatCSV(writer, csvFormatNoHeader);
                }
            }
        }
    }

    @Override
    public void beforeImport() {
        SQLDialect dialectFamily = ctx.configuration().dialect().family();

        boolean isOk = dialectFamily == SQLDialect.MYSQL || dialectFamily == SQLDialect.MARIADB;
        if (!isOk) {
            throw new IllegalStateException("Unsupported dialect " + dialectFamily);
        }

        ctx.execute("set foreign_key_checks=0");
    }

    @Override
    public void afterImport() {
        ctx.execute("set foreign_key_checks=1");
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
            .fields(getTableFields(table))
            .execute();

        if (!CollectionUtils.isEmpty(loader.errors())) {
            for (LoaderError error : loader.errors()) {
                log.error("Exception happened", error.exception());
            }
            throw new RuntimeException("There were errors loading data into table " + table.getName());
        }

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

        ctx.execute(DSL.sql("ALTER TABLE {0} AUTO_INCREMENT = {1}", table, DSL.val(nextVal)));
    }

    // -------------------------------------------------------------------------
    // Loader API cannot import temporal values in ISO8601 UTC format into a
    // Timestamp. More context: https://groups.google.com/g/jooq-user/c/VzZdIT7Xdnc
    //
    // Because of this, we are overriding the default converter of TIMESTAMP
    // table fields during the import.
    // -------------------------------------------------------------------------

    private List<Field<?>> getTableFields(Table<?> table) {
        return Arrays.stream(table.fields())
            .map(it -> {
                if (it.getDataType().isTimestamp()) {
                    return DSL.field(it.getName(), SQLDataType.VARCHAR(50)).convert(isoTimestampConverter);
                } else {
                    return it;
                }
            }).toList();
    }

    private static class IsoTimestampConverter extends AbstractConverter<String, Timestamp> {

        private IsoTimestampConverter() {
            super(String.class, Timestamp.class);
        }

        @Override
        public Timestamp from(String str) {
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            return Timestamp.from(Instant.parse(str));
        }

        @Override
        public String to(Timestamp ts) {
            return ts == null ? null : ts.toString();
        }
    }

}

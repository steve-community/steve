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

import de.rwth.idsg.steve.repository.DataImportExportRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import static jooq.steve.db.tables.WebUser.WEB_USER;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class DataImportExportRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private DataImportExportRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void exportCsv() {
        StringWriter out = new StringWriter();
        assertNoDatabaseException(() -> repository.exportCsv(out, WEB_USER));
        Assertions.assertNotNull(out.toString());
    }

    @Test
    public void beforeImportAndAfterExport() {
        assertNoDatabaseException(repository::beforeImport);
        assertNoDatabaseException(repository::afterImport);
    }

    @Test
    public void importCsv() {
        Integer before = dslContext.selectCount().from(WEB_USER).fetchOne(0, int.class);
        assertNoDatabaseException(() -> repository.importCsv(new ByteArrayInputStream(new byte[0]), WEB_USER));
        Integer after = dslContext.selectCount().from(WEB_USER).fetchOne(0, int.class);
        Assertions.assertEquals(before, after);
    }
}

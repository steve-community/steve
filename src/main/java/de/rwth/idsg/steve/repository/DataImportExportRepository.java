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
package de.rwth.idsg.steve.repository;

import org.jooq.Table;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.11.2025
 */
public interface DataImportExportRepository {

    void exportCsv(Writer writer, Table<?> table) throws IOException;

    void beforeImport();
    void afterImport();
    void importCsv(InputStream in, Table<?> table) throws IOException;
}

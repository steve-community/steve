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
package de.rwth.idsg.steve.repository.dto;

import jooq.steve.db.tables.records.TransactionStartRecord;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The data of a transaction, as read from the database.
 *
 * @param values               its intermediate meter values
 * @param nextTransactionStart start event of the subsequent transaction at the same charge box and connector
 */
public record TransactionContext(
    Transaction transaction,
    List<TransactionMeterValue> values,
    @Nullable TransactionStartRecord nextTransactionStart
) {
}

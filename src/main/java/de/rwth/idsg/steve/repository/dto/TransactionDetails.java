/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.04.2016
 */
@Getter
@RequiredArgsConstructor
public class TransactionDetails {
    private final Transaction transaction;
    private final List<MeterValues> values;

    /**
     * Subsequent transaction's start event (to the transaction that we give details about),
     * that is at the same chargebox and connector
     */
    @Nullable
    private final TransactionStartRecord nextTransactionStart;

    @Getter
    @Builder
    public static class MeterValues {
        private final DateTime valueTimestamp;
        private final String value, readingContext, format, measurand, location, unit;

        // New in OCPP 1.6
        private final String phase;
    }
}

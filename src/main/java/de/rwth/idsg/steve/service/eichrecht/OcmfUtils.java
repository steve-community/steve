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
package de.rwth.idsg.steve.service.eichrecht;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Set;

public final class OcmfUtils {

    static final String PREFIX = "OCMF|";

    static final Set<String> BEGIN_TYPES = Set.of("B");

    /**
     * Besides E (end), a transaction can also stop with L (stopped locally), R (stopped remotely),
     * A (aborted) or P (power failure). Same as the S.A.F.E. transparency software.
     */
    static final Set<String> END_TYPES = Set.of("E", "L", "R", "A", "P");

    private OcmfUtils() {
    }

    /**
     * Pagination "T" marks transaction records, "F" fiscal records. Like the S.A.F.E. transparency software,
     * we only treat "T" records as transaction data.
     */
    static boolean isTransactionRecord(OcmfRecord record) {
        String pagination = record.payload().pagination();
        return pagination != null && pagination.length() > 1 && pagination.startsWith("T");
    }

    @Nullable
    static OcmfRecord.Reading beginReading(OcmfRecord record) {
        return findReading(record, BEGIN_TYPES);
    }

    @Nullable
    static OcmfRecord.Reading endReading(OcmfRecord record) {
        return findReading(record, END_TYPES);
    }

    /**
     * @return null, if the unit is neither kWh nor Wh
     */
    @Nullable
    static BigDecimal valueInWh(OcmfRecord.Reading reading) {
        if (reading.value() == null || reading.unit() == null) {
            return null;
        }
        return switch (reading.unit().trim()) {
            case "kWh" -> reading.value().movePointRight(3);
            case "Wh" -> reading.value();
            default -> null;
        };
    }

    @Nullable
    private static OcmfRecord.Reading findReading(OcmfRecord record, Set<String> types) {
        var readings = record.payload().readings();
        if (readings == null) {
            return null;
        }
        for (var reading : readings) {
            if (reading != null
                && reading.transactionType() != null
                && types.contains(reading.transactionType().trim())) {
                return reading;
            }
        }
        return null;
    }
}

/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.utils;

import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OcppTagActivityRecordUtils {

    public static boolean isExpired(OcppTagActivityRecord record, DateTime now) {
        DateTime expiry = record.getExpiryDate();
        return expiry != null && now.isAfter(expiry);
    }

    public static boolean isBlocked(OcppTagActivityRecord record) {
        return record.getMaxActiveTransactionCount() == 0;
    }

    public static boolean reachedLimitOfActiveTransactions(OcppTagActivityRecord record) {
        int max = record.getMaxActiveTransactionCount();

        // blocked
        if (max == 0) {
            return true;
        }

        // allow all
        if (max < 0) {
            return false;
        }

        // allow as specified
        return record.getActiveTransactionCount() >= max;
    }
}

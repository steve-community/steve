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
package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QueryPeriodType {
    ALL("All", -1),
    TODAY("Today", -1),
    LAST_10("Last 10 days", 10),
    LAST_30("Last 30 days", 30),
    LAST_90("Last 90 days", 90),
    FROM_TO("From/To", -1);

    @Getter
    private final String value;
    private final int interval;

    public int getInterval() {
        if (this.interval == -1) {
            throw new UnsupportedOperationException("This enum does not have any meaningful interval set.");
        }
        return this.interval;
    }

    public static QueryPeriodType fromValue(String v) {
        for (QueryPeriodType c: QueryPeriodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

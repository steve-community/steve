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
package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
public class TransactionQueryForm extends QueryForm {

    // Internal database Id
    private Integer transactionPk;

    private boolean returnCSV;

    @NotNull(message = "Query type is required")
    private QueryType type;

    private QueryPeriodType periodType;

    /**
     * Init with sensible default values
     */
    public TransactionQueryForm() {
        returnCSV = false;
        periodType = QueryPeriodType.ALL;
        type = QueryType.ACTIVE;
    }

    @AssertTrue(message = "The values 'From' and 'To' must be both set")
    public boolean isPeriodFromToCorrect() {
        return periodType != QueryPeriodType.FROM_TO || isFromToSet();
    }

    public boolean isTransactionPkSet() {
        return transactionPk != null;
    }

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    public enum QueryType {
        ALL("All"),
        ACTIVE("Active");

        @Getter private final String value;

        public static QueryType fromValue(String v) {
            for (QueryType c: QueryType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

    @RequiredArgsConstructor
    public enum QueryPeriodType {
        ALL("All", -1),
        TODAY("Today", -1),
        LAST_10("Last 10 days", 10),
        LAST_30("Last 30 days", 30),
        LAST_90("Last 90 days", 90),
        FROM_TO("From/To", -1);

        @Getter private final String value;
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
}

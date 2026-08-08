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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@ToString(callSuper = true)
public class TransactionQueryForm extends QueryForm {

    // Internal database Id
    @Schema(description = "Database primary keys of the transactions")
    private List<@NotNull(message = "transactionPk must not be null") Integer> transactionPk;
    
    @Schema(description = "ID of the connector")
    private Integer connectorId;

    @Schema(description = "Disabled for the Web APIs. Do not use and set", hidden = true)
    private boolean returnCSV = false;

    @Schema(description = "Return active or all transactions? Defaults to ALL")
    private QueryType type = QueryType.ACTIVE;

    @Schema(description = "Return the time period of the transactions. If FROM_TO, 'from' and 'to' must be set. Additionally, 'to' must be after 'from'. Defaults to ALL")
    private QueryPeriodType periodType = QueryPeriodType.ALL;

    @Schema(hidden = true)
    @AssertTrue(message = "The values 'From' and 'To' must be both set")
    public boolean isPeriodFromToCorrect() {
        return periodType != QueryPeriodType.FROM_TO || isFromToSet();
    }

    @Schema(hidden = true)
    public boolean isTransactionPkSet() {
        return !CollectionUtils.isEmpty(transactionPk);
    }
    
    @Schema(hidden = true)
    public boolean isConnectorIdSet() {
        return connectorId != null;
    }


    public QueryType getType() {
        return Objects.requireNonNullElse(type, QueryType.ALL);
    }

    public QueryPeriodType getPeriodType() {
        return Objects.requireNonNullElse(periodType, QueryPeriodType.ALL);
    }

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    public enum QueryType {
        ALL("All"),
        ACTIVE("Active"),
        STOPPED("Stopped");

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

    @ToString(callSuper = true)
    public static class TransactionQueryFormForApi extends TransactionQueryForm {

        public TransactionQueryFormForApi() {
            super();
            setType(QueryType.ALL);
            setPeriodType(QueryPeriodType.ALL);
        }
    }
}

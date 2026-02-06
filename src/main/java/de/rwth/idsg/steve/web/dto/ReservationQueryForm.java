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

import de.rwth.idsg.steve.repository.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ReservationQueryForm extends QueryForm {

    private List<@Positive(message = "reservationId has to be a positive number") Integer> reservationId;

    private List<@Positive(message = "transactionId has to be a positive number") Integer> transactionId;

    private ReservationStatus status;

    private QueryPeriodType periodType;

    /**
     * Init with sensible default values
     */
    public ReservationQueryForm() {
        periodType = QueryPeriodType.ACTIVE;
    }

    @Schema(hidden = true)
    public boolean isStatusSet() {
        return status != null;
    }

    @Schema(hidden = true)
    public boolean isReservationIdSet() {
        return !CollectionUtils.isEmpty(reservationId);
    }

    @Schema(hidden = true)
    public boolean isTransactionIdSet() {
        return !CollectionUtils.isEmpty(transactionId);
    }

    @AssertTrue(message = "The values 'From' and 'To' must be both set")
    public boolean isPeriodFromToCorrect() {
        return periodType != QueryPeriodType.FROM_TO || isFromToSet();
    }

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    public enum QueryPeriodType {
        ACTIVE("Active"),
        FROM_TO("From/To");

        @Getter private final String value;

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

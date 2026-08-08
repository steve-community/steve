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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import jakarta.validation.constraints.AssertTrue;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QueryPeriodFromToFilter {

    @Schema(description = "Show results that happened after this date/time. Format: ISO 8601 with timezone. Example: `2024-08-25T14:30:00.000Z`")
    private DateTime from;

    @Schema(description = "Show results that happened before this date/time. Format: ISO 8601 with timezone. Example: `2024-08-25T14:30:00.000Z`")
    private DateTime to;

    @Schema(hidden = true)
    @AssertTrue(message = "'To' must be after 'From'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    @Schema(hidden = true)
    boolean isFromToSet() {
        return from != null && to != null;
    }
}

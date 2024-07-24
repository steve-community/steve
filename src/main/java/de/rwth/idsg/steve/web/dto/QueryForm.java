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
package de.rwth.idsg.steve.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class QueryForm {

    @Schema(description = "The identifier of the chargebox (i.e. charging station)")
    private String chargeBoxId;

    @Schema(description = "The OCPP tag")
    private String ocppIdTag;

    @Schema(description = "Show results that happened after this date/time. Format: ISO8601 without timezone. Example: `2022-10-10T09:00:00`")
    private LocalDateTime from;

    @Schema(description = "Show results that happened before this date/time. Format: ISO8601 without timezone. Example: `2022-10-10T12:00:00`")
    private LocalDateTime to;

    @Schema(hidden = true)
    @AssertTrue(message = "'To' must be after 'From'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    @Schema(hidden = true)
    boolean isFromToSet() {
        return from != null && to != null;
    }

    @Schema(hidden = true)
    public boolean isChargeBoxIdSet() {
        return chargeBoxId != null;
    }

    @Schema(hidden = true)
    public boolean isOcppIdTagSet() {
        return ocppIdTag != null;
    }
}

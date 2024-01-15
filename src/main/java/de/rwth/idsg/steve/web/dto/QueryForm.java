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

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class QueryForm {

    @ApiModelProperty(value = "The identifier of the chargebox (i.e. charging station)")
    private String chargeBoxId;

    @ApiModelProperty(value = "The OCPP tag")
    private String ocppIdTag;

    @ApiModelProperty(value = "Show results that happened after this date/time. Format: ISO8601 without timezone. Example: `2022-10-10T09:00:00`")
    private LocalDateTime from;

    @ApiModelProperty(value = "Show results that happened before this date/time. Format: ISO8601 without timezone. Example: `2022-10-10T12:00:00`")
    private LocalDateTime to;

    @ApiModelProperty(hidden = true)
    @AssertTrue(message = "'To' must be after 'From'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    @ApiModelProperty(hidden = true)
    boolean isFromToSet() {
        return from != null && to != null;
    }

    @ApiModelProperty(hidden = true)
    public boolean isChargeBoxIdSet() {
        return chargeBoxId != null;
    }

    @ApiModelProperty(hidden = true)
    public boolean isOcppIdTagSet() {
        return ocppIdTag != null;
    }
}

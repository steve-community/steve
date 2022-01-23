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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class QueryForm {

    private String chargeBoxId;
    private String ocppIdTag;

    private LocalDateTime from;
    private LocalDateTime to;

    @AssertTrue(message = "'To' must be after 'From'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    boolean isFromToSet() {
        return from != null && to != null;
    }

    public boolean isChargeBoxIdSet() {
        return chargeBoxId != null;
    }

    public boolean isOcppIdTagSet() {
        return ocppIdTag != null;
    }
}

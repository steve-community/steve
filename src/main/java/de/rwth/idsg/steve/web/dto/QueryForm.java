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
import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class QueryForm extends QueryPeriodFromToFilter {

    @Schema(description = "The identifiers of the chargebox (i.e. charging station)")
    private List<String> chargeBoxId;

    @Schema(description = "The OCPP tags")
    private List<String> ocppIdTag;

    @Schema(description = "The User IDs")
    private List<@Positive(message = "userId has to be a positive number") Integer> userId;

    @Schema(hidden = true)
    public boolean isChargeBoxIdSet() {
        return !CollectionUtils.isEmpty(chargeBoxId);
    }

    @Schema(hidden = true)
    public boolean isOcppIdTagSet() {
        return !CollectionUtils.isEmpty(ocppIdTag);
    }

    @Schema(hidden = true)
    public boolean isUserIdSet() {
        return !CollectionUtils.isEmpty(userId);
    }
}

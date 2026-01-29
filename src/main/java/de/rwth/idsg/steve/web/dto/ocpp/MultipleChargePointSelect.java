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
package de.rwth.idsg.steve.web.dto.ocpp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.AssertTrue;

import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Getter
@Setter
public class MultipleChargePointSelect implements ChargePointSelection {

    /**
     * Only for the Web pages
     */
    @Schema(hidden = true)
    private List<ChargePointSelect> chargePointSelectList = Collections.emptyList();

    /**
     * Only for the APIs
     */
    @ArraySchema(
        // Schema for the ARRAY itself
        arraySchema = @Schema(
            description = "Should contain at least 1 element",
            requiredMode = Schema.RequiredMode.REQUIRED
        ),
        // Schema for the ITEMS inside the array
        schema = @Schema(
            description = "The identifier of the chargebox (i.e. charging station)"
        ),
        minItems = 1
    )
    private List<String> chargeBoxIdList = Collections.emptyList();

    @JsonIgnore
    @AssertTrue(message = "Please select at least 1 charge point")
    public boolean isValidChargePointSelectList() {
        if (!CollectionUtils.isEmpty(chargeBoxIdList)) {
            return true;
        }

        return !CollectionUtils.isEmpty(chargePointSelectList);
    }

    @JsonIgnore
    @AssertTrue(message = "Charge Box ID list should contain at least 1 element")
    public boolean isValidChargeBoxIdList() {
        if (!CollectionUtils.isEmpty(chargePointSelectList)) {
            return true;
        }

        return !CollectionUtils.isEmpty(chargeBoxIdList);
    }
}

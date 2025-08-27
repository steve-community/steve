/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.web.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */
@Getter
@RequiredArgsConstructor
public class ApiChargePointList {
    @Schema(description = "List of charge points")
    private final List<ChargePointInfo> chargePointList = new ArrayList<>();

    public void addCP(String chargeBoxId, List<Integer> connectorIds) {
        ChargePointInfo cp = new ChargePointInfo(chargeBoxId, connectorIds);
        this.chargePointList.add(cp);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ChargePointInfo {
        @Schema(description = "Charge Box ID")
        private final String chargeBoxId;

        @Schema(description = "List of the charge box connectors")
        private final List<Integer> connectorIds;
    }
}

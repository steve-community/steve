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
package de.rwth.idsg.steve.gateway.ocpi.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * OCPI v2.2 CdrDimensionType enum
 *
 * @author Steve Community
 */
public enum CdrDimensionType {
    CURRENT("CURRENT"),
    ENERGY("ENERGY"),
    ENERGY_EXPORT("ENERGY_EXPORT"),
    ENERGY_IMPORT("ENERGY_IMPORT"),
    MAX_CURRENT("MAX_CURRENT"),
    MIN_CURRENT("MIN_CURRENT"),
    MAX_POWER("MAX_POWER"),
    MIN_POWER("MIN_POWER"),
    PARKING_TIME("PARKING_TIME"),
    POWER("POWER"),
    RESERVATION_TIME("RESERVATION_TIME"),
    STATE_OF_CHARGE("STATE_OF_CHARGE"),
    TIME("TIME");

    private final String value;

    CdrDimensionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
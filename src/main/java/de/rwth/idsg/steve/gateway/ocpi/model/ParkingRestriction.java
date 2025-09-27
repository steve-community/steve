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
 * OCPI v2.2 ParkingRestriction enum
 *
 * @author Steve Community
 */
public enum ParkingRestriction {
    EV_ONLY("EV_ONLY"),
    PLUGGED("PLUGGED"),
    DISABLED("DISABLED"),
    CUSTOMERS("CUSTOMERS"),
    MOTORCYCLES("MOTORCYCLES");

    private final String value;

    ParkingRestriction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
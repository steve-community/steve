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

import org.jooq.JSON;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum WebUserAuthority {
    USER(JSON.json("[\"USER\"]"), "USER"),
    ADMIN(JSON.json("[\"ADMIN\"]"), "ADMIN"),
    USER_ADMIN(JSON.json("[\"ADMIN\",\"USER\"]"), "USER, ADMIN");

    @Getter private final JSON jsonValue;
    @Getter private final String value;

    public static WebUserAuthority fromJsonValue(JSON v) {
        for (WebUserAuthority c: WebUserAuthority.values()) {
            if (c.jsonValue.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

    public static WebUserAuthority fromValue(String v) {
        for (WebUserAuthority c: WebUserAuthority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

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
package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.11.2025
 */
@RequiredArgsConstructor
@Getter
public enum OcppSecurityProfile {
    Profile_0(0, "0: No HTTP basic authentication, no TLS"),
    Profile_1(1, "1: HTTP basic authentication, no TLS"),
    Profile_2(2, "2: HTTP basic authentication, TLS with server-side certificate"),
    Profile_3(3, "3: TLS with client-side and server-side certificates (mutual TLS or mTLS)");

    private final int value;
    private final String description;

    public static OcppSecurityProfile fromValue(Integer value) {
        if  (value == null) {
            return null;
        }
        for (OcppSecurityProfile c: OcppSecurityProfile.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        throw new IllegalArgumentException(String.valueOf(value));
    }
}

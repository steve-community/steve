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
package de.rwth.idsg.steve.ocpp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.05.2026
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PowerType {

    AC_1_PHASE("AC 1-Phase"),
    AC_2_PHASE("AC 2-Phase"),
    AC_2_PHASE_SPLIT("AC 2-Phase Split"),
    AC_3_PHASE("AC 3-Phase"),
    DC("DC");

    private final String text;

    public static PowerType fromNullable(String enumName) {
        return StringUtils.isEmpty(enumName) ? null : PowerType.valueOf(enumName);
    }
}

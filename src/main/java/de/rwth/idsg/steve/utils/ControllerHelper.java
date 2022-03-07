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
package de.rwth.idsg.steve.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.utils.CountryCodesProvider.getCountryCodes;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerHelper {

    public static final String EMPTY_OPTION = "-- Empty --";

    public static final Map<String, String> COUNTRY_DROPDOWN = getCountryCodes();

    public static Map<String, String> idTagEnhancer(List<String> idTagList) {
        Map<String, String> map = new HashMap<>(idTagList.size() + 1);
        map.put("", EMPTY_OPTION);

        for (String s : idTagList) {
            map.put(s, s);
        }
        return map;
    }


}

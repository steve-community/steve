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
package de.rwth.idsg.steve.utils;

import com.neovisionaries.i18n.CountryCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerHelper {

    public static final String EMPTY_OPTION = "-- Empty --";

    public static final Map<String, String> COUNTRY_DROPDOWN = createCountryDropdown();
    public static final Map<String, String> TIME_ZONE_DROPDOWN = createTimeZoneDropdown();

    public static Map<String, String> idTagEnhancer(List<String> idTagList) {
        Map<String, String> map = new HashMap<>(idTagList.size() + 1);
        map.put("", EMPTY_OPTION);

        for (String s : idTagList) {
            map.put(s, s);
        }
        return map;
    }

    private static Map<String, String> createCountryDropdown() {
        CountryCode[] codes = CountryCode.values();
        Arrays.sort(codes, Comparator.comparing(CountryCode::getName));

        Map<String, String> map = new LinkedHashMap<>(codes.length + 1);
        map.put("", EMPTY_OPTION);

        for (CountryCode c : codes) {
            if (shouldInclude(c)) {
                map.put(c.getAlpha2(), c.getName());
            }
        }
        return map;
    }

    /**
     * There are some invalid codes like {@link CountryCode#UNDEFINED} and {@link CountryCode#EU},
     * or some countries are listed twice {@link CountryCode#FI} - {@link CountryCode#SF} and
     * {@link CountryCode#GB} - {@link CountryCode#UK} which are confusing. We filter these out.
     */
    private static boolean shouldInclude(CountryCode c) {
        return c.getAssignment() == CountryCode.Assignment.OFFICIALLY_ASSIGNED;
    }

    private static Map<String, String> createTimeZoneDropdown() {
        var availableZoneIds = ZoneId.getAvailableZoneIds();
        Map<String, String> map = new LinkedHashMap<>(availableZoneIds.size() + 1);
        map.put("", EMPTY_OPTION);

        availableZoneIds.stream()
            .sorted()
            .forEach(id -> map.put(id, id));

        return map;
    }

}

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerHelperTest {

    @Test
    public void countryDropdown() {
        Map<String, String> countries = ControllerHelper.COUNTRY_DROPDOWN;
        List<String> countryNames = new ArrayList<>(countries.values());

        Assertions.assertEquals(ControllerHelper.EMPTY_OPTION, countries.get(""));
        Assertions.assertEquals("Germany", countries.get("DE"));
        Assertions.assertFalse(countries.containsKey("EU"));

        List<String> sortedCountryNames = countryNames.subList(1, countryNames.size()).stream().sorted().toList();
        Assertions.assertEquals(sortedCountryNames, countryNames.subList(1, countryNames.size()));
    }

    @Test
    public void timeZoneDropdown() {
        Map<String, String> timeZoneIds = ControllerHelper.TIME_ZONE_DROPDOWN;
        List<String> keys = new ArrayList<>(timeZoneIds.keySet());

        Assertions.assertEquals("", keys.getFirst());
        Assertions.assertEquals(ControllerHelper.EMPTY_OPTION, timeZoneIds.get(""));
        Assertions.assertEquals("Europe/Berlin", timeZoneIds.get("Europe/Berlin"));

        List<String> sortedZoneIds = ZoneId.getAvailableZoneIds().stream().sorted().toList();
        Assertions.assertEquals(sortedZoneIds, keys.subList(1, keys.size()));
    }
}

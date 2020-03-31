/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public final class ControllerHelper {
    private ControllerHelper() { }

    public static final String EMPTY_OPTION = "-- Empty --";

    public static final Map<String, String> COUNTRY_DROPDOWN = populateCountryCodes();

    public static Address recordToDto(AddressRecord record) {
        Address address = new Address();
        if (record != null) {
            address.setAddressPk(record.getAddressPk());
            address.setStreet(record.getStreet());
            address.setHouseNumber(record.getHouseNumber());
            address.setZipCode(record.getZipCode());
            address.setCity(record.getCity());
            address.setCountry(CountryCode.getByCode(record.getCountry()));
        }
        return address;
    }

    public static Map<String, String> idTagEnhancer(List<String> idTagList) {
        Map<String, String> map = new HashMap<>(idTagList.size() + 1);
        map.put("", EMPTY_OPTION);

        for (String s : idTagList) {
            map.put(s, s);
        }
        return map;
    }

    private static Map<String, String> populateCountryCodes() {
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
}

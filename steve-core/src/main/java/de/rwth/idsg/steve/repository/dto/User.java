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
package de.rwth.idsg.steve.repository.dto;

import com.neovisionaries.i18n.CountryCode;
import de.rwth.idsg.steve.web.dto.Address;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
public class User {

    @Getter
    @Builder
    public static final class Overview {
        private final Integer userPk;
        private final String name, phone, email;
        private final List<OcppTagEntry> ocppTagEntries;
    }

    @Getter
    @Builder
    public static final class Details {
        // from UserRecord
        private final Integer userPk;
        private final String firstName;
        private final String lastName;
        private final LocalDate birthDay;
        private final String phone;
        private final String sex;
        private final String eMail;
        private final String note;
        // from AddressRecord
        private final String street;
        private final String houseNumber;
        private final String zipCode;
        private final String city;
        private final CountryCode country;
        // from OcppTag
        private final List<OcppTagEntry> ocppTagEntries;

        public Address getAddress() {
            var address = new Address();
            address.setStreet(street);
            address.setHouseNumber(houseNumber);
            address.setZipCode(zipCode);
            address.setCity(city);
            address.setCountry(country);
            return address;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static final class OcppTagEntry {
        private final Integer ocppTagPk;
        private final String idTag;
    }
}

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
package de.rwth.idsg.steve.utils.mapper;

import com.neovisionaries.i18n.CountryCode;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressMapper {

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
}

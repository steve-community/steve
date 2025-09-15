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
package de.rwth.idsg.steve.jooq.mapper;

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.UserRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User.Details fromRecord(UserRecord r, Address address, List<User.OcppTagEntry> ocppTagEntries) {
        var builder = User.Details.builder()
                .userPk(r.getUserPk())
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .birthDay(r.getBirthDay())
                .phone(r.getPhone())
                .sex(r.getSex())
                .eMail(r.getEMail())
                .note(r.getNote())
                .ocppTagEntries(ocppTagEntries);

        if (address != null) {
            builder.street(address.getStreet())
                    .houseNumber(address.getHouseNumber())
                    .zipCode(address.getZipCode())
                    .city(address.getCity())
                    .country(address.getCountry());
        }

        return builder.build();
    }
}

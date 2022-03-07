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

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserSex;
import jooq.steve.db.tables.records.UserRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserFormMapper {

    public static UserForm toForm(User.Details details) {
        UserRecord userRecord = details.getUserRecord();

        UserForm form = new UserForm();
        form.setUserPk(userRecord.getUserPk());
        form.setFirstName(userRecord.getFirstName());
        form.setLastName(userRecord.getLastName());
        form.setBirthDay(userRecord.getBirthDay());
        form.setPhone(userRecord.getPhone());
        form.setSex(UserSex.fromDatabaseValue(userRecord.getSex()));
        form.setEMail(userRecord.getEMail());
        form.setNote(userRecord.getNote());
        form.setAddress(AddressMapper.recordToDto(details.getAddress()));
        form.setOcppIdTag(details.getOcppIdTag().orElse(ControllerHelper.EMPTY_OPTION));

        return form;
    }
}

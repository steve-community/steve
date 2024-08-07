/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import de.rwth.idsg.steve.repository.dto.WebUser;
import de.rwth.idsg.steve.web.dto.WebUserForm;
import java.util.List;
import jooq.steve.db.tables.records.WebusersRecord;
import jooq.steve.db.tables.records.WebauthoritiesRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

 /**
 * @author fnkbsi
 * @since 01.04.2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebUserFormMapper {

    public static WebUserForm toForm(WebUser.Details details) {
        WebusersRecord webuserRecord = details.getWebusersRecord();
        List<WebauthoritiesRecord> authRecords = details.getWebauthoritiesRecordList();

        WebUserForm form = new WebUserForm();
        form.setWebusername(webuserRecord.getUsername());
        form.setEnabled(webuserRecord.getEnabled());

        form.setRoles(rolesStr(authRecords));

        return form;
    }

    private static String rolesStr(List<WebauthoritiesRecord> authRecords) {
        String roles = "";

        for (WebauthoritiesRecord ar : authRecords) {
            roles = roles + ar.getAuthority() + "; ";
        }
        roles = roles.strip();
        if (!roles.isBlank()) { //(roles.endsWith(";"))
            roles = roles.substring(0, roles.length() - 1);
        }

        return roles;
    }
}

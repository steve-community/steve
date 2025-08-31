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
package de.rwth.idsg.steve.repository.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Select;

import static jooq.steve.db.Tables.OCPP_TAG;
import static jooq.steve.db.Tables.USER_OCPP_TAG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2025
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepositoryUtils {

    public static Select<Record1<String>> ocppTagByUserIdQuery(DSLContext ctx, int userId) {
        return ctx.select(OCPP_TAG.ID_TAG)
                .from(OCPP_TAG)
                .join(USER_OCPP_TAG)
                .on(USER_OCPP_TAG.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK))
                .where(USER_OCPP_TAG.USER_PK.eq(userId));
    }
}

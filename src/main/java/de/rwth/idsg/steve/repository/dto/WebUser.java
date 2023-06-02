/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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

import java.util.List;
import jooq.steve.db.tables.records.WebauthoritiesRecord;
import jooq.steve.db.tables.records.WebusersRecord;
import lombok.Builder;
import lombok.Getter;

//import java.util.Optional;

 /**
 * @author Frank Brosi
 * @since 01.04.2022
 */
public class WebUser {

    @Getter
    @Builder
    public static final class Overview {
        private final Boolean enabled;
        private final String webusername, roles;
    }

    @Getter
    @Builder
    public static final class Details {
        private final WebusersRecord webusersRecord;
        private final List<WebauthoritiesRecord> webauthoritiesRecordList;
    }
}

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

import de.rwth.idsg.steve.NotificationFeature;
import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.UserRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        private final List<NotificationFeature> enabledFeatures;
    }

    @Getter
    @Builder
    public static final class Details {
        private final UserRecord userRecord;
        private final AddressRecord address;
        private final List<OcppTagEntry> ocppTagEntries;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class OcppTagEntry {
        private final Integer ocppTagPk;
        private final String idTag;
    }

}

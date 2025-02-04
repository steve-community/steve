/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;


/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.09.2015
 */
public class DateTimeConverter  {

    public static DateTime from(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return new DateTime(timestamp.getTime());
        }
    }

    public static DateTime from(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(Timestamp.valueOf(date).getTime());
        }
    }

    public static Timestamp to(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return new Timestamp(dateTime.getMillis());
        }
    }

    public static LocalDateTime toDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return LocalDateTime.ofInstant(dateTime.toDate().toInstant(), ZoneId.systemDefault());
        }
    }
}

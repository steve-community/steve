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
package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.jooq.Converter;

import java.sql.Timestamp;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.09.2015
 */
public class DateTimeConverter implements Converter<Timestamp, DateTime> {

    @Override
    public DateTime from(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return new DateTime(timestamp.getTime());
        }
    }

    @Override
    public Timestamp to(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return new Timestamp(dateTime.getMillis());
        }
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<DateTime> toType() {
        return DateTime.class;
    }
}

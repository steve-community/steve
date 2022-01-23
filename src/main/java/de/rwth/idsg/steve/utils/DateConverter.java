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
package de.rwth.idsg.steve.utils;

import org.joda.time.LocalDate;
import org.jooq.Converter;

import java.sql.Date;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
public class DateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate from(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        } else {
            return new LocalDate(sqlDate.getTime());
        }
    }

    @Override
    public Date to(LocalDate jodaDate) {
        if (jodaDate == null) {
            return null;
        } else {
            return new Date(jodaDate.toDateTimeAtStartOfDay().getMillis());
        }
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }
}

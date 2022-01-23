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
package de.rwth.idsg.steve.web;

import com.google.common.base.Strings;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 04.01.2015
 */
public class LocalDateTimeEditor extends PropertyEditorSupport {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        } else {
            return DATE_TIME_FORMATTER.print((LocalDateTime) value);
        }
    }

    @Override
    public void setAsText(String text) {
        if (Strings.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(DATE_TIME_FORMATTER.parseLocalDateTime(text));
        }
    }
}

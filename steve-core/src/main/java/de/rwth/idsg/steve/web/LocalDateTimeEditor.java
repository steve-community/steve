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
package de.rwth.idsg.steve.web;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 04.01.2015
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeEditor extends PropertyEditorSupport {

    private final DateTimeFormatter dateTimeFormatter;

    public static LocalDateTimeEditor forMvc() {
        return new LocalDateTimeEditor(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTimeEditor forApi() {
        return new LocalDateTimeEditor(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localdatetime) {
            return dateTimeFormatter.format(localdatetime);
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to LocalDateTime");
    }

    @Override
    public void setAsText(String text) {
        if (Strings.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(LocalDateTime.parse(text, dateTimeFormatter));
        }
    }
}

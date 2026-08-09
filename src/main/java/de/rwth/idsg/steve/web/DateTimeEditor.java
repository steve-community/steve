/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.08.2025
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeEditor extends PropertyEditorSupport {

    private final DateTimeFormatter writeFormatter;
    private final DateTimeFormatter readFormatter;

    public static DateTimeEditor forMvc() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        return new DateTimeEditor(dtf, dtf);
    }

    /**
     * Uses full ISO 8601 date time format, e.g. "2024-08-25T14:30:00.000Z".
     * - Writes: The same as Joda DateTime's toString() method {@link AbstractInstant#toString()}
     * - Reads: A forgiving reader, that does not require all fields to be there. Accepts all ISO 8601 formats,
     * e.g. "2024-08-25", "2024-08-25T14:30", "2024-08-25T14:30:00Z", etc. The missing fields will be filled with
     * default values (time: 00:00:00.000, timezone: system default).
     */
    public static DateTimeEditor forApi() {
        return new DateTimeEditor(
            ISODateTimeFormat.dateTime(),
            ISODateTimeFormat.dateTimeParser()
        );
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        } else {
            return writeFormatter.print((DateTime) value);
        }
    }

    @Override
    public void setAsText(String text) {
        if (Strings.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(readFormatter.parseDateTime(text));
        }
    }
}

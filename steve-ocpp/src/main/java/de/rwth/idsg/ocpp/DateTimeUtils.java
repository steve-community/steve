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
package de.rwth.idsg.ocpp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    // Flexible ISO-8601 parser: supports the optional fraction and optional offset.
    // ISO_LOCAL_DATE_TIME cannot be used because SECOND_OF_MINUTE are required here.
    public static final DateTimeFormatter OCPP_DATETIME_PARSER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("uuuu-MM-dd'T'HH:mm:ss")

            // Optional: .SSS... (fractional seconds)
            .optionalStart()
            .appendLiteral('.')
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
            .optionalEnd()

            // Optional: +02:00 or Z (offset)
            .optionalStart()
            .appendOffsetId()
            .optionalEnd()

            .parseStrict()
            .toFormatter();

    // ISO-8601 formatter: outputs timestamps with fixed 3-digits nanosecond precision.
    // ISO_LOCAL_DATE_TIME cannot be used because it does not support the fixed 3-digits nanosecond precision.
    // Note: PARSER and FORMATTER are not interchangeable because PARSER is flexible.
    public static final DateTimeFormatter OCPP_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("uuuu-MM-dd'T'HH:mm:ss.SSS")
            .parseLenient()
            .appendOffsetId()
            .parseStrict()
            .toFormatter();

    public static OffsetDateTime toOffsetDateTime(String value, ZoneId fallbackZoneId) {
        if (value == null || value.isBlank()) {
            return null;
        }
        var parsed = OCPP_DATETIME_PARSER.parse(value);
        if (!parsed.isSupported(ChronoField.OFFSET_SECONDS)) {
            if (fallbackZoneId == null) {
                throw new IllegalArgumentException("No offset and no fallback zone id provided");
            }
            // Input has no offset → assume fallback zone
            var ldt = LocalDateTime.from(parsed);
            var offset = fallbackZoneId.getRules().getOffset(ldt);
            return ldt.atOffset(offset);
        }
        return OffsetDateTime.from(parsed);
    }

    public static String toString(OffsetDateTime dateTime, ZoneId zoneId) {
        if (dateTime == null) {
            return null;
        }
        if (zoneId == null) {
            // Convert to UTC before formatting.
            // From specification: OCPP does not prescribe the use of a specific time zone for time values.
            // However, it is strongly recommended to use UTC for all time values to improve interoperability
            // between Central Systems and Charge Points.
            dateTime = dateTime.withOffsetSameInstant(ZoneOffset.UTC);
        } else {
            var targetOffset = zoneId.getRules().getOffset(dateTime.toInstant());
            dateTime = dateTime.withOffsetSameInstant(targetOffset);
        }
        return OCPP_DATETIME_FORMATTER.format(dateTime);
    }
}

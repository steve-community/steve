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
package de.rwth.idsg.steve.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final DateTimeFormatter HUMAN_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        } else {
            return ldt.atOffset(ZoneOffset.UTC);
        }
    }

    public static LocalDateTime toLocalDateTime(OffsetDateTime dt) {
        if (dt == null) {
            return null;
        } else {
            return dt.toLocalDateTime();
        }
    }

    /**
     * Print the date/time nicer, if it's from today, yesterday or tomorrow.
     */
    public static String humanize(OffsetDateTime dt) {
        if (dt == null) {
            return "";
        }
        return humanize(dt);
    }

    public static String humanize(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }

        // Equalize time fields before comparing date fields
        var inputAtMidnight = dt.toLocalDate().atStartOfDay();
        var todayAtMidnight = LocalDateTime.now().toLocalDate().atStartOfDay();

        // Is it today?
        if (inputAtMidnight.equals(todayAtMidnight)) {
            return "Today at " + TIME_FORMATTER.format(dt);

        // Is it yesterday?
        } else if (inputAtMidnight.equals(todayAtMidnight.minusDays(1))) {
            return "Yesterday at " + TIME_FORMATTER.format(dt);

        // Is it tomorrow?
        } else if (inputAtMidnight.equals(todayAtMidnight.plusDays(1))) {
            return "Tomorrow at " + TIME_FORMATTER.format(dt);

        // So long ago OR in the future...
        } else {
            return HUMAN_FORMATTER.format(dt);
        }
    }

    public static String timeElapsed(LocalDateTime from, LocalDateTime to) {
        var duration = Duration.between(from, to).abs();

        var days = duration.toDays();
        duration = duration.minusDays(days);

        var hours = duration.toHours();
        duration = duration.minusHours(hours);

        var minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        var seconds = duration.getSeconds();

        var sb = new StringBuilder();
        if (days > 0) sb.append(days).append(days == 1 ? " day" : " days").append(" ");
        if (hours > 0) sb.append(hours).append(hours == 1 ? " hour" : " hours").append(" ");
        if (minutes > 0) sb.append(minutes).append(minutes == 1 ? " minute" : " minutes").append(" ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append(seconds == 1 ? " second" : " seconds");

        return sb.toString().trim();
    }

    public static long getOffsetFromUtcInSeconds() {
        var offset = ZonedDateTime.now().getOffset();
        return offset.getTotalSeconds();
    }
}

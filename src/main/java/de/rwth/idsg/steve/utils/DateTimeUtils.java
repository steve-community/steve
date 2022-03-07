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

import de.rwth.idsg.steve.SteveException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.jooq.DSLContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final DateTimeFormatter HUMAN_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd 'at' HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendDays().appendSuffix(" day", " days").appendSeparator(" ")
            .appendHours().appendSuffix(" hour", " hours").appendSeparator(" ")
            .appendMinutes().appendSuffix(" minute", " minutes").appendSeparator(" ")
            .appendSeconds().appendSuffix(" second", " seconds")
            .toFormatter();

    public static DateTime toDateTime(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        } else {
            return ldt.toDateTime();
        }
    }

    public static LocalDateTime toLocalDateTime(DateTime dt) {
        if (dt == null) {
            return null;
        } else {
            return dt.toLocalDateTime();
        }
    }

    /**
     * Print the date/time nicer, if it's from today, yesterday or tomorrow.
     */
    public static String humanize(DateTime dt) {
        if (dt == null) {
            return "";
        }

        // Equalize time fields before comparing date fields
        DateTime inputAtMidnight = dt.withTimeAtStartOfDay();
        DateTime todayAtMidnight = DateTime.now().withTimeAtStartOfDay();

        // Is it today?
        if (inputAtMidnight.equals(todayAtMidnight)) {
            return "Today at " + TIME_FORMATTER.print(dt);

        // Is it yesterday?
        } else if (inputAtMidnight.equals(todayAtMidnight.minusDays(1))) {
            return "Yesterday at " + TIME_FORMATTER.print(dt);

        // Is it tomorrow?
        } else if (inputAtMidnight.equals(todayAtMidnight.plusDays(1))) {
            return "Tomorrow at " + TIME_FORMATTER.print(dt);

        // So long ago OR in the future...
        } else {
            return HUMAN_FORMATTER.print(dt);
        }
    }

    public static String timeElapsed(DateTime from, DateTime to) {
        return PERIOD_FORMATTER.print(new Period(from, to));
    }

    public static void checkJavaAndMySQLOffsets(DSLContext ctx) {
        long sql = CustomDSL.selectOffsetFromUtcInSeconds(ctx);
        long java = DateTimeUtils.getOffsetFromUtcInSeconds();

        if (sql != java) {
            throw new SteveException("MySQL and Java are not using the same time zone. " +
                    "Java offset in seconds (%s) != MySQL offset in seconds (%s)", java, sql);
        }
    }

    private static long getOffsetFromUtcInSeconds() {
        DateTimeZone timeZone = DateTimeZone.getDefault();
        DateTime now = DateTime.now();
        long offsetInMilliseconds = timeZone.getOffset(now.getMillis());
        return TimeUnit.MILLISECONDS.toSeconds(offsetInMilliseconds);
    }
}

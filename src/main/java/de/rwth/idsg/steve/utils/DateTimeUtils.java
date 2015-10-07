package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
public final class DateTimeUtils {
    private DateTimeUtils() {}

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
        return (ldt == null) ? null : ldt.toDateTime();
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
}
package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
public final class DateTimeUtils {
    private DateTimeUtils() {}

    private static final DateTimeFormatter HUMAN_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd 'at' HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new DateTime().getMillis());
    }

    public static Timestamp toTimestamp(LocalDateTime ldt) {
        return (ldt == null) ? null : new Timestamp(ldt.toDateTime().getMillis());
    }

    public static DateTime toDateTime(LocalDateTime ldt) {
        return (ldt == null) ? null : ldt.toDateTime();
    }

    /**
     * Print the date/time nicer, if it's from today, yesterday or tomorrow.
     */
    public static String humanize(Timestamp ts) {
        if (ts == null) return "";

        DateTime input = new DateTime(ts);

        // Equalize time fields before comparing date fields
        DateTime inputAtMidnight = input.withTimeAtStartOfDay();
        DateTime todayAtMidnight = new DateTime().withTimeAtStartOfDay();

        // Is it today?
        if (inputAtMidnight.equals(todayAtMidnight)) {
            return "Today at " + TIME_FORMATTER.print(input);

        // Is it yesterday?
        } else if (inputAtMidnight.equals(todayAtMidnight.minusDays(1))) {
            return "Yesterday at " + TIME_FORMATTER.print(input);

        // Is it tomorrow?
        } else if (inputAtMidnight.equals(todayAtMidnight.plusDays(1))) {
            return "Tomorrow at " + TIME_FORMATTER.print(input);

        // So long ago OR in the future...
        } else {
            return HUMAN_FORMATTER.print(input);
        }
    }
}
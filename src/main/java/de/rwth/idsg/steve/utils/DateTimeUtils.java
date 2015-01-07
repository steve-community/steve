package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public final class DateTimeUtils {
    private DateTimeUtils() {}

    private static final DateTimeFormatter HUMAN_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd 'at' HH:mm");

    public static Timestamp getCurrentTimestamp(){
        return new Timestamp(new DateTime().getMillis());
    }

    public static Timestamp toTimestamp(LocalDateTime ldt) {
        return (ldt == null) ? null : new Timestamp(ldt.toDateTime().getMillis());
    }

    public static DateTime toDateTime(LocalDateTime ldt) {
        return (ldt == null) ? null : ldt.toDateTime();
    }

    /**
     * Converts a Timestamp to a String of the pattern "yyyy-MM-dd 'at' HH:mm".
     */
    private static String toString(Timestamp ts){
        if (ts == null) {
            return "";
        }

        long timeLong = ts.getTime();
        return HUMAN_FORMATTER.print(timeLong);
    }

    /**
     * Print the date/time nicer, if it's from today, yesterday or tomorrow.
     */
    public static String humanize(Timestamp ts){
        if (ts == null) {
            return "";
        }

        DateTime input = new DateTime(ts);
        DateTime now = new DateTime();

        String result;

        // Equalize time fields before comparing date fields
        DateTime inputAtMidnight = input.withTimeAtStartOfDay();
        DateTime todayAtMidnight = now.withTimeAtStartOfDay();

        // Is it today?
        if (inputAtMidnight.equals(todayAtMidnight)) {
            result = "Today at " + DateTimeFormat.forPattern("HH:mm").print(input);

//			PeriodFormatter pf = new PeriodFormatterBuilder()
//					.printZeroNever()
//					.appendHours().appendSuffix(" hour ", " hours ")
//					.appendMinutes().appendSuffix(" minute ", " minutes ")
//					.toFormatter();
//			
//			String elapsed = pf.print(new Period(input, now));			
//			if (elapsed.length() == 0) elapsed = "Less than a minute ";			
//			result.append(elapsed).append(" ago");				

        // Is it yesterday?
        } else if (inputAtMidnight.equals(todayAtMidnight.minusDays(1))) {
            result = "Yesterday at " + DateTimeFormat.forPattern("HH:mm").print(input);

        // Is it tomorrow?
        } else if (inputAtMidnight.equals(todayAtMidnight.plusDays(1))) {
            result = "Tomorrow at " + DateTimeFormat.forPattern("HH:mm").print(input);

        // So long ago OR in the future...
        } else {
            result = toString(ts);
        }

        return result;
    }
}
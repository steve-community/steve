package net.parkl.ocpp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalendarUtils {

    public static LocalDateTime getLastMomentOfDay(LocalDateTime date) {
        return LocalDateTime.of(date.toLocalDate(), LocalTime.MAX);
    }

    public static LocalDateTime getFirstMomentOfDay(LocalDateTime date) {
        return LocalDateTime.of(date.toLocalDate(), LocalTime.MIN);
    }

    public static LocalDateTime createDaysBeforeNow(int days) {
        return LocalDateTime.now().minusDays(days);
    }
}

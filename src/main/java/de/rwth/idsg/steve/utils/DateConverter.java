package de.rwth.idsg.steve.utils;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.jooq.Converter;

import java.sql.Date;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public class DateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate from(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        } else {
            return new LocalDate(sqlDate.getTime(), DateTimeZone.UTC);
        }
    }

    @Override
    public Date to(LocalDate jodaDate) {
        if (jodaDate == null) {
            return null;
        } else {
            return new Date(jodaDate.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis());
        }
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }
}

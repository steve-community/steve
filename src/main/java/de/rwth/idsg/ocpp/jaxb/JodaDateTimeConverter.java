package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import static org.joda.time.format.ISODateTimeFormat.date;

/**
 * Joda-Time and XSD represent data and time information according to ISO 8601.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.10.2014
 */
public class JodaDateTimeConverter extends XmlAdapter<String, DateTime> {

    private static final DateTimeFormatter formatter = dateTimeParser();

    @Override
    public DateTime unmarshal(String v) throws Exception {
        if (isNullOrEmpty(v)) {
            return null;
        } else {
            return DateTime.parse(v, formatter);
        }
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return v.toString();
        }
    }

    /**
     * Because I did not want to include Guava or similar only for this.
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * A custom DateTimeFormatter that follows the strictness and flexibility of XSD:dateTime (ISO 8601).
     * This exact composition (with optional fields) is not present under {@link org.joda.time.format.ISODateTimeFormat}.
     */
    private static DateTimeFormatter dateTimeParser() {
        return new DateTimeFormatterBuilder()
                .append(date())
                .appendLiteral('T')
                .append(hourElement())
                .append(minuteElement())
                .append(secondElement())
                .appendOptional(fractionElement().getParser())
                .appendOptional(offsetElement().getParser())
                .toFormatter();
    }

    // -------------------------------------------------------------------------
    // Copy-paste from "private" methods in ISODateTimeFormat
    // -------------------------------------------------------------------------

    private static DateTimeFormatter hourElement() {
        return new DateTimeFormatterBuilder()
                .appendHourOfDay(2)
                .toFormatter();
    }

    private static DateTimeFormatter minuteElement() {
        return new DateTimeFormatterBuilder()
                .appendLiteral(':')
                .appendMinuteOfHour(2)
                .toFormatter();
    }

    private static DateTimeFormatter secondElement() {
        return new DateTimeFormatterBuilder()
                .appendLiteral(':')
                .appendSecondOfMinute(2)
                .toFormatter();
    }

    private static DateTimeFormatter fractionElement() {
        return new DateTimeFormatterBuilder()
                .appendLiteral('.')
                // Support parsing up to nanosecond precision even though
                // those extra digits will be dropped.
                .appendFractionOfSecond(3, 9)
                .toFormatter();
    }

    private static DateTimeFormatter offsetElement() {
        return new DateTimeFormatterBuilder()
                .appendTimeZoneOffset("Z", true, 2, 4)
                .toFormatter();
    }
}
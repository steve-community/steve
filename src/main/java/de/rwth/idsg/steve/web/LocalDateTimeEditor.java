package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.utils.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 04.01.2015
 */
public class LocalDateTimeEditor extends PropertyEditorSupport {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        } else {
            return DATE_TIME_FORMATTER.print((LocalDateTime) value);
        }
    }

    @Override
    public void setAsText(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(DATE_TIME_FORMATTER.parseLocalDateTime(text));
        }
    }
}
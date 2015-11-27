package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.utils.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public class LocalDateEditor extends PropertyEditorSupport {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        } else {
            return DATE_FORMATTER.print((LocalDate) value);
        }
    }

    @Override
    public void setAsText(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(DATE_FORMATTER.parseLocalDate(text));
        }
    }
}

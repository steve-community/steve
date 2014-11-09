package de.rwth.idsg.steve.web;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
public class LocalDateEditor extends PropertyEditorSupport {

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public String getAsText() {
        String text = "";
        Object value = getValue();
        if (value != null) {
            text = dateFormatter.print((LocalDate) value);
        }
        return text;
    }

    @Override
    public void setAsText(String text) {
        try {
            setValue(dateFormatter.parseDateTime(text));
        } catch (IllegalArgumentException e) {
            setValue(null);
        }
    }

}
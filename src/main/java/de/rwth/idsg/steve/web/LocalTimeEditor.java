package de.rwth.idsg.steve.web;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
public class LocalTimeEditor extends PropertyEditorSupport {

    private static DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

    @Override
    public String getAsText() {
        String text = "";
        Object value = getValue();
        if (value != null) {
            text = timeFormatter.print((LocalTime) value);
        }
        return text;
    }

    @Override
    public void setAsText(String text) {
        try {
            setValue(LocalTime.parse(text, timeFormatter));
        } catch (IllegalArgumentException e) {
            setValue(null);
        }
    }
}
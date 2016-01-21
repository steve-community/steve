package de.rwth.idsg.steve.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.core.convert.converter.Converter;

import java.beans.PropertyEditorSupport;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.01.2016
 */
public class BatchInsertConverter extends PropertyEditorSupport implements Converter<String, List<String>> {

    private static final Splitter SPLITTER = Splitter.on("\r\n").trimResults().omitEmptyStrings();
    private static final Joiner JOINER = Joiner.on("\r\n").skipNulls();

    // when submitting form
    @Override
    public void setAsText(String text) {
        setValue(convert(text));
    }

    // when displaying form
    @Override
    @SuppressWarnings("unchecked")
    public String getAsText() {
        Object o = this.getValue();
        if (o == null) {
            return "";
        } else {
            List<String> list = (List<String>) o;
            return JOINER.join(list);
        }
    }

    @Override
    public List<String> convert(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return Collections.emptyList();
        } else {
            return SPLITTER.splitToList(text);
        }
    }
}

/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.core.convert.converter.Converter;

import java.beans.PropertyEditorSupport;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
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

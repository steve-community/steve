/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.web.api;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.beans.PropertyEditorSupport;
import java.time.Instant;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 04.01.2015
 */
@Slf4j
@RequiredArgsConstructor
public class InstantEditor extends PropertyEditorSupport {

    @Override
    public @Nullable String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant.toString();
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to Instant");
    }

    @Override
    public void setAsText(String text) {
        if (Strings.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(Instant.parse(text));
        }
    }
}

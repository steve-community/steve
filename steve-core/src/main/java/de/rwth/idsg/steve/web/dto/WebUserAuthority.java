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
package de.rwth.idsg.steve.web.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.jooq.JSON;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum WebUserAuthority {
    USER("USER"),
    ADMIN("ADMIN"),
    USER_ADMIN("USER", "ADMIN");

    private final Set<String> values;

    @Getter
    private final JSON jsonValue;

    WebUserAuthority(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("JSON values must not be null or empty");
        }
        this.values = new HashSet<>(Arrays.asList(values));
        this.jsonValue = this.values.stream()
                .map(v -> "\"" + v + "\"")
                .reduce((a, b) -> a + ", " + b)
                .map(s -> JSON.json("[" + s + "]"))
                .orElseThrow(() -> new IllegalArgumentException("Failed to create JSON value"));
    }

    // For jsp
    public String getValue() {
        return String.join(", ", values);
    }

    public static WebUserAuthority fromJsonValue(ObjectMapper mapper, JSON v) {
        try {
            var values = new HashSet<>(Arrays.asList(mapper.readValue(v.data(), String[].class)));
            for (WebUserAuthority c : WebUserAuthority.values()) {
                if (c.values.equals(values)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v.toString());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(v.toString());
        }
    }
}

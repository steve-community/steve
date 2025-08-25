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
package de.rwth.idsg.steve.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.TimeZone;
import java.util.stream.Stream;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.08.2024
 */
public class DateTimeEditorTest {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.forID("UTC"));
    }

    // -------------------------------------------------------------------------
    // MVC tests (format: "yyyy-MM-dd HH:mm")
    // -------------------------------------------------------------------------

    @Test
    public void mvcTestToTextNull() {
        DateTimeEditor mvcEditor = DateTimeEditor.forMvc();
        mvcEditor.setValue(null);

        String asText = mvcEditor.getAsText();
        Assertions.assertNull(asText);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForMvcTestToJava")
    public void mvcTestToJava(String input, DateTime expected) {
        DateTimeEditor apiEditor = DateTimeEditor.forMvc();
        apiEditor.setAsText(input);

        DateTime val = (DateTime) apiEditor.getValue();
        Assertions.assertEquals(expected, val);
        if (expected != null) {
            Assertions.assertEquals(expected.getZone(), val.getZone());
        }
    }

    private static Stream<Arguments> provideArgsForMvcTestToJava() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("2024-06-26 14:30", new DateTime(2024, 6, 26, 14, 30, 0))
        );
    }

    // -------------------------------------------------------------------------
    // API tests (format: ISO 8601 date time)
    // -------------------------------------------------------------------------

    @Test
    public void apiTestToText() {
        DateTime ldt = new DateTime(2024, 6, 26, 14, 30, 0);

        DateTimeEditor apiEditor = DateTimeEditor.forApi();
        apiEditor.setValue(ldt);

        String asText = apiEditor.getAsText();
        Assertions.assertEquals("2024-06-26T14:30:00.000Z", asText);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForApiTestToJava")
    public void apiTestToJava(String input, DateTime expected) {
        DateTimeEditor apiEditor = DateTimeEditor.forApi();
        apiEditor.setAsText(input);

        DateTime val = (DateTime) apiEditor.getValue();
        Assertions.assertEquals(expected, val);
    }

    private static Stream<Arguments> provideArgsForApiTestToJava() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("2024-06-26T14:30:28.000Z", new DateTime(2024, 6, 26, 14, 30, 28)),
            Arguments.of("2024-06-26T14:30:28.000", new DateTime(2024, 6, 26, 14, 30, 28)),
            Arguments.of("2024-06-26T14:30:28Z", new DateTime(2024, 6, 26, 14, 30, 28)),
            Arguments.of("2024-06-26T14:30:28", new DateTime(2024, 6, 26, 14, 30, 28)),
            Arguments.of("2024-06-26T14:30Z", new DateTime(2024, 6, 26, 14, 30, 0)),
            Arguments.of("2024-06-26T14:30", new DateTime(2024, 6, 26, 14, 30, 0)),
            Arguments.of("2024-06-26T14Z", new DateTime(2024, 6, 26, 14, 0, 0)),
            Arguments.of("2024-06-26T14", new DateTime(2024, 6, 26, 14, 0, 0)),
            Arguments.of("2024-06-26TZ", new DateTime(2024, 6, 26, 0, 0, 0)),
            Arguments.of("2024-06-26", new DateTime(2024, 6, 26, 0, 0, 0))
        );
    }

}

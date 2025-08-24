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

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.08.2024
 */
public class LocalDateTimeEditorTest {

    // -------------------------------------------------------------------------
    // MVC tests (format: "yyyy-MM-dd HH:mm")
    // -------------------------------------------------------------------------

    @Test
    public void mvcTestToTextNull() {
        LocalDateTimeEditor mvcEditor = LocalDateTimeEditor.forMvc();
        mvcEditor.setValue(null);

        String asText = mvcEditor.getAsText();
        Assertions.assertNull(asText);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForMvcTestToJava")
    public void mvcTestToJava(String input, LocalDateTime expected) {
        LocalDateTimeEditor apiEditor = LocalDateTimeEditor.forMvc();
        apiEditor.setAsText(input);

        LocalDateTime val = (LocalDateTime) apiEditor.getValue();
        Assertions.assertEquals(expected, val);
    }

    private static Stream<Arguments> provideArgsForMvcTestToJava() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("2024-06-26 14:30", new LocalDateTime(2024, 6, 26, 14, 30, 0))
        );
    }

    // -------------------------------------------------------------------------
    // API tests (format: ISO local date time)
    // -------------------------------------------------------------------------

    @Test
    public void apiTestToText() {
        LocalDateTime ldt = new LocalDateTime(2024, 6, 26, 14, 30, 0);

        LocalDateTimeEditor apiEditor = LocalDateTimeEditor.forApi();
        apiEditor.setValue(ldt);

        assertThrows(UnsupportedOperationException.class, apiEditor::getAsText);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForApiTestToJava")
    public void apiTestToJava(String input, LocalDateTime expected) {
        LocalDateTimeEditor apiEditor = LocalDateTimeEditor.forApi();
        apiEditor.setAsText(input);

        LocalDateTime val = (LocalDateTime) apiEditor.getValue();
        Assertions.assertEquals(expected, val);
    }

    private static Stream<Arguments> provideArgsForApiTestToJava() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("2024-06-26T14:30:28", new LocalDateTime(2024, 6, 26, 14, 30, 28)),
            Arguments.of("2024-06-26T14:30", new LocalDateTime(2024, 6, 26, 14, 30, 0)),
            Arguments.of("2024-06-26T14", new LocalDateTime(2024, 6, 26, 14, 0, 0)),
            Arguments.of("2024-06-26", new LocalDateTime(2024, 6, 26, 0, 0, 0))
        );
    }

}

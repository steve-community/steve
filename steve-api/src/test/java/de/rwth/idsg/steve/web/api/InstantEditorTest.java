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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.08.2025
 */
public class InstantEditorTest {

    private static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");
    private static final ZoneId UTC = UTC_TZ.toZoneId();

    @Test
    public void apiTestToText() {
        var ldt = LocalDateTime.of(2024, 6, 26, 14, 30, 0);
        var apiEditor = new InstantEditor();

        apiEditor.setValue(ldt.toInstant(ZoneOffset.UTC));
        var asText = apiEditor.getAsText();

        assertThat(asText).isEqualTo("2024-06-26T14:30:00Z");
    }

    @ParameterizedTest
    @MethodSource("provideArgsForApiTestToJava")
    public void apiTestToJava(String input, LocalDateTime expected) {
        var apiEditor = new InstantEditor();

        apiEditor.setAsText(input);
        var val = (Instant) apiEditor.getValue();

        if (expected == null) {
            assertThat(val).isNull();
        } else {
            assertThat(val.atZone(UTC)).isEqualTo(expected.atZone(UTC));
        }
    }

    private static Stream<Arguments> provideArgsForApiTestToJava() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", null),
                Arguments.of("2024-06-26T14:30:28.000Z", LocalDateTime.of(2024, 6, 26, 14, 30, 28)),
                Arguments.of("2024-06-26T14:30:28Z", LocalDateTime.of(2024, 6, 26, 14, 30, 28))
        );
    }

    @ParameterizedTest
    @MethodSource("provideFailArgsForApiTestToJava")
    public void apifailTestToJava(String input) {
        var apiEditor = new InstantEditor();

        assertThatExceptionOfType(DateTimeParseException.class)
                .isThrownBy(() -> apiEditor.setAsText(input));
    }

    private static Stream<Arguments> provideFailArgsForApiTestToJava() {
        return Stream.of(
                Arguments.of("2024-06-26T14:30:28.000"),
                Arguments.of("2024-06-26T14:30:28"),
                Arguments.of("2024-06-26T14:30Z"),
                Arguments.of("2024-06-26T14:30"),
                Arguments.of("2024-06-26T14Z"),
                Arguments.of("2024-06-26T14"),
                Arguments.of("2024-06-26TZ"),
                Arguments.of("2024-06-26")
        );
    }
}

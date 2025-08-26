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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.08.2025
 */
public class InstantEditorTest {

    private static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");
    private static final ZoneId UTC = UTC_TZ.toZoneId();

    @Test
    public void mvcTestToTextNull() {
        var mvcEditor = new InstantEditor(ZoneOffset.UTC);

        mvcEditor.setValue(null);
        var asText = mvcEditor.getAsText();

        assertThat(asText).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideArgsForMvcTestToJava")
    public void mvcTestToJava(String input, LocalDateTime expected) {
        var apiEditor = new InstantEditor(ZoneOffset.UTC);

        apiEditor.setAsText(input);
        var val = (Instant) apiEditor.getValue();

        if (expected == null) {
            assertThat(val).isNull();
        } else {
            assertThat(val.atZone(UTC)).isEqualTo(expected.atZone(UTC));
        }
    }

    private static Stream<Arguments> provideArgsForMvcTestToJava() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("2024-06-26 14:30", LocalDateTime.of(2024, 6, 26, 14, 30, 0))
        );
    }
}

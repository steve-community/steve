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
package de.rwth.idsg.ocpp.jaxb;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static de.rwth.idsg.ocpp.DateTimeUtils.toOffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.byLessThan;

class JavaDateTimeConverterTest {

    // -------------------------------------------------------------------------
    // Marshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideZoneIds")
    void testMarshalNullInput(ZoneId zoneId) {
        var converterUtc = new JavaDateTimeConverter(zoneId, true);
        var valUtc = converterUtc.marshal(null);
        assertThat(valUtc).isNull();
        var converter = new JavaDateTimeConverter(zoneId, false);
        var val = converter.marshal(null);
        assertThat(val).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideValidMarshallingInput")
    void testMarshalUtcValidInput(ZoneId zoneId, OffsetDateTime val, String expected, boolean marshallToUtc) {
        var converter = new JavaDateTimeConverter(zoneId, marshallToUtc);
        var output = converter.marshal(val);
        assertThat(output).isEqualTo(expected);
    }

    private static Stream<Arguments> provideValidMarshallingInput() {
        var marchallUtcInputs = Stream.of( //
                        Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z", true),
                        Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z", true),
                        Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z", true),
                        Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z", true),
                        Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z", true),
                        Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z", true),
                        Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z", true),
                        Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z", true))
                .flatMap(input -> ZONE_IDS.stream()
                        .map(tz -> Arguments.of( //
                                tz, //
                                toOffsetDateTime((String) input.get()[0], ZoneId.of("UTC")), // val
                                input.get()[1], // expected
                                input.get()[2] // marshallToUtc
                                )));
        var marchallTzInputs = Stream.of( //
                        Arguments.of("UTC", "2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z", false),
                        Arguments.of(
                                "Europe/Berlin", "2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00", false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00", false),
                        Arguments.of("UTC", "2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z", false),
                        Arguments.of("Europe/Berlin", "2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00", false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00", false),
                        Arguments.of("UTC", "2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z", false),
                        Arguments.of(
                                "Europe/Berlin", "2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00", false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00", false),
                        Arguments.of("UTC", "2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z", false),
                        Arguments.of(
                                "Europe/Berlin",
                                "2022-06-30T01:20:52.126+05:00",
                                "2022-06-29T22:20:52.126+02:00",
                                false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00", false),
                        Arguments.of("UTC", "2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z", false),
                        Arguments.of(
                                "Europe/Berlin", "2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00", false),
                        Arguments.of("-05:00", "2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00", false),
                        Arguments.of("UTC", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z", false),
                        Arguments.of("Europe/Berlin", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00", false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00", false),
                        Arguments.of("UTC", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z", false),
                        Arguments.of(
                                "Europe/Berlin", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00", false),
                        Arguments.of("-05:00", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00", false),
                        Arguments.of("UTC", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z", false),
                        Arguments.of(
                                "Europe/Berlin", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28", false),
                        Arguments.of("-05:00", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00", false) //
                        )
                .map(input -> Arguments.of( //
                        ZoneId.of((String) input.get()[0]), //
                        toOffsetDateTime((String) input.get()[1], ZoneId.of((String) input.get()[0])), // val
                        input.get()[2], // expected
                        input.get()[3] // marshallToUtc
                        ));
        return Stream.concat(marchallUtcInputs, marchallTzInputs);
    }

    // -------------------------------------------------------------------------
    // Unmarshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideValidUnmarshallingInput")
    void testUnmarshalValid(ZoneId zoneId, String val, ZonedDateTime expected) {
        var converter = new JavaDateTimeConverter(zoneId, true);
        var actual = converter.unmarshal(val);
        assertThat(actual).isCloseTo(expected.toOffsetDateTime(), byLessThan(1, ChronoUnit.SECONDS));
    }

    @ParameterizedTest
    @MethodSource("provideZoneIds")
    void testUnmarshalNullInput(ZoneId zoneId) {
        var converter = new JavaDateTimeConverter(zoneId, true);
        assertThatCode(() -> converter.unmarshal(null)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("provideValidEmptyUnmarshallingInput")
    void testUnmarshalEmptyInput(ZoneId zoneId, String val) {
        var converter = new JavaDateTimeConverter(zoneId, true);
        assertThatCode(() -> converter.unmarshal(val)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUnmarshallingInput")
    void testUnmarshalInvalid(ZoneId zoneId, String val) {
        var converter = new JavaDateTimeConverter(zoneId, true);
        assertThatExceptionOfType(DateTimeParseException.class).isThrownBy(() -> converter.unmarshal(val));
    }

    private static final List<ZoneId> ZONE_IDS = Stream.of( //
                    "UTC", //
                    "-05:00", // "EST" is deprecated (like all other short ids)
                    "Europe/Berlin", //
                    "America/New_York", //
                    "Asia/Tokyo", //
                    "Australia/Sydney" //
                    )
            .map(ZoneId::of)
            .toList();

    private static Stream<Arguments> provideZoneIds() {
        return ZONE_IDS.stream().map(Arguments::of);
    }

    private static Stream<Arguments> provideValidUnmarshallingInput() {
        var validInputs = Stream.of( //
                        Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z"),
                        Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00[Europe/Berlin]"),
                        Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00[America/New_York]"),
                        Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z"),
                        Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00[Europe/Berlin]"),
                        Arguments.of("2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00[America/New_York]"),
                        Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z"),
                        Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00[Europe/Berlin]"),
                        Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00[America/New_York]"),
                        Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z"),
                        Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T22:20:52.126+02:00[Europe/Berlin]"),
                        Arguments.of(
                                "2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00[America/New_York]"),
                        Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z"),
                        Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00[Europe/Berlin]"),
                        Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00[America/New_York]"))
                .flatMap(input -> ZONE_IDS.stream()
                        .map(tz -> Arguments.of(tz, input.get()[0], ZonedDateTime.parse((String) input.get()[1]))));
        var withoutOffsetInputs = Stream.of( //
                tz("UTC", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z"),
                tz("Europe/Berlin", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00[Europe/Berlin]"),
                tz("-05:00", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00[America/New_York]"),
                tz("UTC", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z"),
                tz("Europe/Berlin", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00[Europe/Berlin]"),
                tz("-05:00", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00[America/New_York]"),
                tz("UTC", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z"),
                tz("Europe/Berlin", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28[Europe/Berlin]"),
                tz("-05:00", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00[America/New_York]") //
                );
        return Stream.concat(validInputs, withoutOffsetInputs);
    }

    private static Arguments tz(String zoneId, String input, String expectedZdt) {
        return Arguments.of(ZoneId.of(zoneId), input, ZonedDateTime.parse(expectedZdt));
    }

    private static Stream<Arguments> provideValidEmptyUnmarshallingInput() {
        return Stream.of( //
                        "", //
                        "      " //
                        )
                .flatMap(input -> ZONE_IDS.stream().map(tz -> Arguments.of(tz, input)));
    }

    private static Stream<Arguments> provideInvalidUnmarshallingInput() {
        return Stream.of( //
                        "-1", //
                        "10000", // https://github.com/steve-community/steve/issues/1292
                        "text", //
                        "2022-06-30", // no time
                        "2022-06-30T01:20", // seconds are required
                        "2022-06-30T25:20:34", // hour out of range
                        "22-06-30T25:20:34" // year not YYYY-format
                        )
                .flatMap(input -> ZONE_IDS.stream().map(tz -> Arguments.of(tz, input)));
    }
}

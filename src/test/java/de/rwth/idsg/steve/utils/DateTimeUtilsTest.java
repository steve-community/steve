/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class DateTimeUtilsTest {

    @Test
    public void testToDateTime_nullInput() {
        DateTime result = DateTimeUtils.toDateTime(null);
        Assertions.assertNull(result);
    }

    @Test
    public void testToDateTime_preservesInstantAndOffset() {
        OffsetDateTime input = OffsetDateTime.parse("2026-03-01T10:15:30.123+05:30");

        DateTime result = DateTimeUtils.toDateTime(input);

        Assertions.assertEquals(input.toInstant().toEpochMilli(), result.getMillis());
        Assertions.assertEquals(input.getOffset().getTotalSeconds() * 1000, result.getZone().getOffset(result.getMillis()));
    }

    @Test
    public void testToOffsetDateTime_usesProvidedZone() {
        DateTime input = DateTime.parse("2026-01-15T12:00:00.000Z");
        ZoneId zoneId = ZoneId.of("Europe/Berlin");

        OffsetDateTime result = DateTimeUtils.toOffsetDateTime(input, zoneId);

        Assertions.assertEquals(input.toDate().toInstant(), result.toInstant());
        Assertions.assertEquals(ZoneOffset.ofHours(1), result.getOffset());
    }
}

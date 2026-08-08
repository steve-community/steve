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
package de.rwth.idsg.steve.web.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeZoneIdValidatorTest {

    private final TimeZoneIdValidator validator = new TimeZoneIdValidator();

    @Test
    public void nullIsValid() {
        Assertions.assertTrue(validator.isValid(null, null));
    }

    @Test
    public void emptyIsValid() {
        Assertions.assertTrue(validator.isValid("", null));
    }

    @Test
    public void availableZoneIdIsValid() {
        Assertions.assertTrue(validator.isValid("Europe/Berlin", null));
    }

    @Test
    public void unknownZoneIdIsInvalid() {
        Assertions.assertFalse(validator.isValid("Europe/Not-A-Time-Zone", null));
    }
}

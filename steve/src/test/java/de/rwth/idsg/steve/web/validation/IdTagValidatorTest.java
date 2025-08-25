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
package de.rwth.idsg.steve.web.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.03.2021
 */
public class IdTagValidatorTest {

    private final IdTagValidator validator = new IdTagValidator();

    @Test
    public void testNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void testAllLowercaseLetters() {
        assertThat(validator.isValid("test", null)).isTrue();
    }

    @Test
    public void testAllUppercaseLetters() {
        assertThat(validator.isValid("TEST", null)).isTrue();
    }

    @Test
    public void testMixedCaseLetters() {
        assertThat(validator.isValid("TesT", null)).isTrue();
        assertThat(validator.isValid("tEst", null)).isTrue();
    }

    @Test
    public void testLettersAndNumbers() {
        assertThat(validator.isValid("test12", null)).isTrue();
        assertThat(validator.isValid("89test", null)).isTrue();
        assertThat(validator.isValid("te9s0t", null)).isTrue();
    }

    @Test
    public void testDot() {
        assertThat(validator.isValid(".test", null)).isTrue();
        assertThat(validator.isValid("test.", null)).isTrue();
        assertThat(validator.isValid("te..st", null)).isTrue();
    }

    @Test
    public void testDash() {
        assertThat(validator.isValid("-test", null)).isTrue();
        assertThat(validator.isValid("test-", null)).isTrue();
        assertThat(validator.isValid("te--st", null)).isTrue();
    }

    @Test
    public void testUnderscore() {
        assertThat(validator.isValid("_test", null)).isTrue();
        assertThat(validator.isValid("test_", null)).isTrue();
        assertThat(validator.isValid("te__st", null)).isTrue();
    }

    /**
     * https://github.com/steve-community/steve/issues/475
     */
    @Test
    public void testColon() {
        assertThat(validator.isValid(":test", null)).isTrue();
        assertThat(validator.isValid("test:", null)).isTrue();
        assertThat(validator.isValid("te::st", null)).isTrue();

        assertThat(validator.isValid("VID:00XXXXXXXXXX", null)).isTrue();
    }

    @Test
    public void testPoundSign() {
        assertThat(validator.isValid("#test", null)).isTrue();
        assertThat(validator.isValid("test#", null)).isTrue();
        assertThat(validator.isValid("te##st", null)).isTrue();

        // Tag provided by Webasto charge points
        // https://github.com/steve-community/steve/pull/1322
        assertThat(validator.isValid("#FreeCharging", null)).isTrue();
    }

    @Test
    public void testCombined() {
        assertThat(validator.isValid("1t.E-S_:t20#", null)).isTrue();
    }
}

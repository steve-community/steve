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
 * @since 01.08.2024
 */
public class ChargeBoxIdValidatorTest {

    private final ChargeBoxIdValidator validator = new ChargeBoxIdValidator((String) null);

    @Test
    public void testNull() {
        assertThat(validator.isValid(null)).isFalse();
    }

    @Test
    public void testEmpty() {
        assertThat(validator.isValid("")).isFalse();
    }

    @Test
    public void testSpace() {
        assertThat(validator.isValid("  ")).isFalse();
    }

    @Test
    public void testAllLowercaseLetters() {
        assertThat(validator.isValid("test")).isTrue();
    }

    @Test
    public void testAllUppercaseLetters() {
        assertThat(validator.isValid("TEST")).isTrue();
    }

    @Test
    public void testMixedCaseLetters() {
        assertThat(validator.isValid("TesT")).isTrue();
        assertThat(validator.isValid("tEst")).isTrue();
    }

    @Test
    public void testLettersAndNumbers() {
        assertThat(validator.isValid("test12")).isTrue();
        assertThat(validator.isValid("89test")).isTrue();
        assertThat(validator.isValid("te9s0t")).isTrue();
    }

    @Test
    public void testDot() {
        assertThat(validator.isValid(".test")).isTrue();
        assertThat(validator.isValid("test.")).isTrue();
        assertThat(validator.isValid("te..st")).isTrue();
    }

    @Test
    public void testDash() {
        assertThat(validator.isValid("-test")).isTrue();
        assertThat(validator.isValid("test-")).isTrue();
        assertThat(validator.isValid("te--st")).isTrue();
    }

    @Test
    public void testUnderscore() {
        assertThat(validator.isValid("_test")).isTrue();
        assertThat(validator.isValid("test_")).isTrue();
        assertThat(validator.isValid("te__st")).isTrue();
    }

    @Test
    public void testColon() {
        assertThat(validator.isValid(":test")).isTrue();
        assertThat(validator.isValid("test:")).isTrue();
        assertThat(validator.isValid("te::st")).isTrue();
        assertThat(validator.isValid("VID:00XXXXXXXXXX")).isTrue();
    }

    @Test
    public void testPoundSign() {
        assertThat(validator.isValid("#test")).isTrue();
        assertThat(validator.isValid("test#")).isTrue();
        assertThat(validator.isValid("te##st")).isTrue();
        assertThat(validator.isValid("#FreeCharging")).isTrue();
    }

    @Test
    public void testCombined() {
        assertThat(validator.isValid("1t.E-S_:t20#")).isTrue();
    }

    @Test
    public void testSpaceAtBeginning() {
        assertThat(validator.isValid(" test")).isFalse();
    }

    @Test
    public void testSpaceAtEnd() {
        assertThat(validator.isValid("test ")).isFalse();
    }

    @Test
    public void testSpaceInMiddle() {
        assertThat(validator.isValid("test1 test2")).isTrue();
    }

    @Test
    public void testOpeningParenthesis() {
        assertThat(validator.isValid("te(st")).isFalse();
    }

    @Test
    public void testClosingParenthesis() {
        assertThat(validator.isValid("te)st")).isFalse();
    }

    @Test
    public void testBiggerSymbol() {
        assertThat(validator.isValid("te>st")).isFalse();
    }

    @Test
    public void testSmallerSymbol() {
        assertThat(validator.isValid("te<st")).isFalse();
    }

    @Test
    public void testSlash() {
        assertThat(validator.isValid("te/st")).isFalse();
    }

    @Test
    public void testEquals() {
        assertThat(validator.isValid("te=st")).isFalse();
    }

    @Test
    public void testSpecialCharAsExample() {
        assertThat(validator.isValid("ÂtestÂ")).isTrue();
    }
}

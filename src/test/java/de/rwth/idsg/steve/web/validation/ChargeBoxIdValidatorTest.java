/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.08.2024
 */
public class ChargeBoxIdValidatorTest {

    ChargeBoxIdValidator validator = new ChargeBoxIdValidator();

    @Test
    public void testNull() {
        Assertions.assertFalse(validator.isValid(null));
    }

    @Test
    public void testEmpty() {
        Assertions.assertFalse(validator.isValid(""));
    }

    @Test
    public void testSpace() {
        Assertions.assertFalse(validator.isValid("  "));
    }

    @Test
    public void testAllLowercaseLetters() {
        Assertions.assertTrue(validator.isValid("test"));
    }

    @Test
    public void testAllUppercaseLetters() {
        Assertions.assertTrue(validator.isValid("TEST"));
    }

    @Test
    public void testMixedCaseLetters() {
        Assertions.assertTrue(validator.isValid("TesT"));
        Assertions.assertTrue(validator.isValid("tEst"));
    }

    @Test
    public void testLettersAndNumbers() {
        Assertions.assertTrue(validator.isValid("test12"));
        Assertions.assertTrue(validator.isValid("89test"));
        Assertions.assertTrue(validator.isValid("te9s0t"));
    }

    @Test
    public void testDot() {
        Assertions.assertTrue(validator.isValid(".test"));
        Assertions.assertTrue(validator.isValid("test."));
        Assertions.assertTrue(validator.isValid("te..st"));
    }

    @Test
    public void testDash() {
        Assertions.assertTrue(validator.isValid("-test"));
        Assertions.assertTrue(validator.isValid("test-"));
        Assertions.assertTrue(validator.isValid("te--st"));
    }

    @Test
    public void testUnderscore() {
        Assertions.assertTrue(validator.isValid("_test"));
        Assertions.assertTrue(validator.isValid("test_"));
        Assertions.assertTrue(validator.isValid("te__st"));
    }

    @Test
    public void testColon() {
        Assertions.assertTrue(validator.isValid(":test"));
        Assertions.assertTrue(validator.isValid("test:"));
        Assertions.assertTrue(validator.isValid("te::st"));
        Assertions.assertTrue(validator.isValid("VID:00XXXXXXXXXX"));
    }

    @Test
    public void testPoundSign() {
        Assertions.assertTrue(validator.isValid("#test"));
        Assertions.assertTrue(validator.isValid("test#"));
        Assertions.assertTrue(validator.isValid("te##st"));
        Assertions.assertTrue(validator.isValid("#FreeCharging"));
    }

    @Test
    public void testCombined() {
        Assertions.assertTrue(validator.isValid("1t.E-S_:t20#"));
    }

    @Test
    public void testSpaceAtBeginning() {
        Assertions.assertFalse(validator.isValid(" test"));
    }

    @Test
    public void testSpaceAtEnd() {
        Assertions.assertFalse(validator.isValid("test "));
    }

    @Test
    public void testSpaceInMiddle() {
        Assertions.assertTrue(validator.isValid("test1 test2"));
    }

    @Test
    public void testOpeningParenthesis() {
        Assertions.assertFalse(validator.isValid("te(st"));
    }

    @Test
    public void testClosingParenthesis() {
        Assertions.assertFalse(validator.isValid("te)st"));
    }

    @Test
    public void testBiggerSymbol() {
        Assertions.assertFalse(validator.isValid("te>st"));
    }

    @Test
    public void testSmallerSymbol() {
        Assertions.assertFalse(validator.isValid("te<st"));
    }

    @Test
    public void testSlash() {
        Assertions.assertFalse(validator.isValid("te/st"));
    }

    @Test
    public void testEquals() {
        Assertions.assertFalse(validator.isValid("te=st"));
    }

    @Test
    public void testSpecialCharAsExample() {
        Assertions.assertTrue(validator.isValid("ÂtestÂ"));
    }
}

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

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.03.2021
 */
public class IdTagTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    public void testNull() {
        assertInvalidWithMessage(null, "must not be null or empty");
    }

    @Test
    public void testBlank() {
        assertInvalidWithMessages(
            "",
            "must not be null or empty",
            "can only contain",
            "size must be between 1 and 20"
        );
        assertInvalidWithMessage(" ", "can only contain");
        assertInvalidWithMessage("\t\n", "can only contain");
    }

    @Test
    public void testAllLowercaseLetters() {
        Assertions.assertTrue(isValid("test"));
    }

    @Test
    public void testAllUppercaseLetters() {
        Assertions.assertTrue(isValid("TEST"));
    }

    @Test
    public void testMixedCaseLetters() {
        Assertions.assertTrue(isValid("TesT"));
        Assertions.assertTrue(isValid("tEst"));
    }

    @Test
    public void testLettersAndNumbers() {
        Assertions.assertTrue(isValid("test12"));
        Assertions.assertTrue(isValid("89test"));
        Assertions.assertTrue(isValid("te9s0t"));
    }

    @Test
    public void testDot() {
        Assertions.assertTrue(isValid(".test"));
        Assertions.assertTrue(isValid("test."));
        Assertions.assertTrue(isValid("te..st"));
    }

    @Test
    public void testDash() {
        Assertions.assertTrue(isValid("-test"));
        Assertions.assertTrue(isValid("test-"));
        Assertions.assertTrue(isValid("te--st"));
    }

    @Test
    public void testUnderscore() {
        Assertions.assertTrue(isValid("_test"));
        Assertions.assertTrue(isValid("test_"));
        Assertions.assertTrue(isValid("te__st"));
    }

    /**
     * https://github.com/steve-community/steve/issues/475
     */
    @Test
    public void testColon() {
        Assertions.assertTrue(isValid(":test"));
        Assertions.assertTrue(isValid("test:"));
        Assertions.assertTrue(isValid("te::st"));

        Assertions.assertTrue(isValid("VID:00XXXXXXXXXX"));
    }

    @Test
    public void testPoundSign() {
        Assertions.assertTrue(isValid("#test"));
        Assertions.assertTrue(isValid("test#"));
        Assertions.assertTrue(isValid("te##st"));

        // Tag provided by Webasto charge points
        // https://github.com/steve-community/steve/pull/1322
        Assertions.assertTrue(isValid("#FreeCharging"));
    }

    @Test
    public void testCombined() {
        Assertions.assertTrue(isValid("1t.E-S_:t20#"));
    }

    @Test
    public void testDefaultMaxLength() {
        Assertions.assertTrue(isValid("12345678901234567890"));
        assertInvalidWithMessage("123456789012345678901", "size must be between 1 and 20");
    }

    private static boolean isValid(String value) {
        return validator.validate(new IdTagValue(value)).isEmpty();
    }

    private static void assertInvalidWithMessage(String value, String expectedMessage) {
        assertInvalidWithMessages(value, expectedMessage);
    }

    private static void assertInvalidWithMessages(String value, String... expectedMessages) {
        var violations = validator.validate(new IdTagValue(value));
        Assertions.assertEquals(expectedMessages.length, violations.size());
        for (String expectedMessage : expectedMessages) {
            Assertions.assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains(expectedMessage)));
        }
    }

    private static class IdTagValue {
        @IdTag
        @Size(min = 1, max = IdTag.MAX_LENGTH)
        private final String value;

        private IdTagValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}

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

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            "must contain between 1 and 20 characters"
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
        assertInvalidWithMessage("123456789012345678901", "must contain between 1 and 20 characters");
    }

    @Test
    public void testConfiguredMaxLength() {
        Assertions.assertTrue(isValidExtended("1234567890123456789012345"));

        var violations = validator.validate(new ExtendedIdTag("12345678901234567890123456"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.iterator().next().getMessage().contains("25"));
    }

    @Test
    public void testOpenApiSchema() {
        var resolved = ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(DefaultIdTag.class));
        var schema = resolved.referencedSchemas.get(DefaultIdTag.class.getSimpleName());
        var valueSchema = (Schema<?>) schema.getProperties().get("value");

        Assertions.assertTrue(schema.getRequired().contains("value"));
        Assertions.assertEquals(1, valueSchema.getMinLength());
        Assertions.assertEquals(IdTag.DEFAULT_MAX_LENGTH, valueSchema.getMaxLength());
        Assertions.assertEquals(IdTag.PATTERN, valueSchema.getPattern());
    }

    @Test
    public void testOpenApiSchemaForListElements() {
        var resolved = ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(DefaultIdTagList.class));
        var schema = resolved.referencedSchemas.get(DefaultIdTagList.class.getSimpleName());
        var listSchema = (Schema<?>) schema.getProperties().get("values");

        Assertions.assertEquals(IdTag.DEFAULT_MAX_LENGTH, listSchema.getItems().getMaxLength());
        Assertions.assertEquals(IdTag.PATTERN, listSchema.getItems().getPattern());
    }

    private static boolean isValid(String value) {
        return validator.validate(new DefaultIdTag(value)).isEmpty();
    }

    private static void assertInvalidWithMessage(String value, String expectedMessage) {
        assertInvalidWithMessages(value, expectedMessage);
    }

    private static void assertInvalidWithMessages(String value, String... expectedMessages) {
        var violations = validator.validate(new DefaultIdTag(value));
        Assertions.assertEquals(expectedMessages.length, violations.size());
        for (String expectedMessage : expectedMessages) {
            Assertions.assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains(expectedMessage)));
        }
    }

    private static boolean isValidExtended(String value) {
        return validator.validate(new ExtendedIdTag(value)).isEmpty();
    }

    private static class DefaultIdTag {
        @IdTag
        private final String value;

        private DefaultIdTag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static class ExtendedIdTag {
        @IdTag(maxLength = 25)
        private final String value;

        private ExtendedIdTag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static class DefaultIdTagList {
        private final List<@IdTag String> values;

        private DefaultIdTagList(List<String> values) {
            this.values = values;
        }

        public List<String> getValues() {
            return values;
        }
    }

}

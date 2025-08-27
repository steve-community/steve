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

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.01.2016
 */
@Component
public class ChargeBoxIdValidator implements ConstraintValidator<ChargeBoxId, String> {

    private static final String REGEX = "[^=/()<>]*";

    @Value("${charge-box-id.validation.regex:#{null}}")
    private String chargeBoxIdValidationRegex;

    private Pattern pattern;

    // Default constructor for Hibernate Validator
    // Spring will inject the value from properties
    // And then HV will call `initialize`
    public ChargeBoxIdValidator() {
        initialize(null);
    }

    @Override
    public void initialize(ChargeBoxId idTag) {
        pattern = Pattern.compile(getRegexToUse(chargeBoxIdValidationRegex));
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string == null) {
            return true; // null is valid, because it is another constraint's responsibility
        }
        return isValid(string);
    }

    public boolean isValid(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return false;
        }

        String str1 = str.strip();
        if (!str1.equals(str)) {
            return false;
        }

        return pattern.matcher(str).matches();
    }

    private static String getRegexToUse(String chargeBoxIdValidationRegex) {
        return Strings.isNullOrEmpty(chargeBoxIdValidationRegex) ? REGEX : chargeBoxIdValidationRegex;
    }
}

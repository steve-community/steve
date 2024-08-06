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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveConfiguration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.01.2016
 */
public class ChargeBoxIdValidator implements ConstraintValidator<ChargeBoxId, String> {

    private static final String REGEX = "[^=/()<>]*";
    private static final Pattern PATTERN = Pattern.compile(getRegexToUse());

    @Override
    public void initialize(ChargeBoxId idTag) {
        // No-op
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

        return PATTERN.matcher(str).matches();
    }

    private static String getRegexToUse() {
        String regexFromConfig = SteveConfiguration.CONFIG.getOcpp().getChargeBoxIdValidationRegex();
        return Strings.isNullOrEmpty(regexFromConfig) ? REGEX : regexFromConfig;
    }
}

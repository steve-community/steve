/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Allowed characters are:
 * Upper or lower case letters, numbers and dot, colon, dash, underscore symbols.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
public class IdTagValidator implements ConstraintValidator<IdTag, String> {

    private static final String IDTAG_PATTERN = "^[a-zA-Z0-9.:_-]{1,20}$";
    private static final Pattern PATTERN = Pattern.compile(IDTAG_PATTERN);

    @Override
    public void initialize(IdTag idTag) {
        // No-op
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return string == null || PATTERN.matcher(string).matches();
    }
}

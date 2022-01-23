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
package de.rwth.idsg.steve;

import static java.lang.String.format;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 28.08.2014
 */
public class SteveException extends RuntimeException {

    private static final long serialVersionUID = 3081743035434873349L;

    public SteveException(String message) {
        super(message);
    }

    public SteveException(String message, Throwable cause) {
        super(message, cause);
    }

    // -------------------------------------------------------------------------
    // No String/variable interpolation in Java. Use format instead.
    // -------------------------------------------------------------------------

    public SteveException(String template, Object arg1) {
        this(format(template, arg1));
    }

    public SteveException(String template, Object arg1, Throwable cause) {
        this(format(template, arg1), cause);
    }

    public SteveException(String template, Object arg1, Object arg2) {
        this(format(template, arg1, arg2));
    }

    public SteveException(String template, Object arg1, Object arg2, Throwable cause) {
        this(format(template, arg1, arg2), cause);
    }
}

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
package de.rwth.idsg.steve;

import static java.lang.String.format;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 28.08.2014
 */
public abstract class SteveException extends RuntimeException {

    private static final long serialVersionUID = 3081743035434873349L;

    protected SteveException(String message) {
        super(message);
    }

    protected SteveException(String message, Throwable cause) {
        super(message, cause);
    }

    // -------------------------------------------------------------------------
    // No String/variable interpolation in Java. Use format instead.
    // -------------------------------------------------------------------------

    protected SteveException(String template, Object arg1) {
        this(format(template, arg1));
    }

    protected SteveException(String template, Object arg1, Throwable cause) {
        this(format(template, arg1), cause);
    }

    protected SteveException(String template, Object arg1, Object arg2) {
        this(format(template, arg1, arg2));
    }

    protected SteveException(String template, Object arg1, Object arg2, Throwable cause) {
        this(format(template, arg1, arg2), cause);
    }

    // -------------------------------------------------------------------------
    // Custom/extending classes
    // -------------------------------------------------------------------------

    public static class InternalError extends SteveException {

        public InternalError(String message) {
            super(message);
        }

        public InternalError(String message, Throwable cause) {
            super(message, cause);
        }

        public InternalError(String template, Object arg1) {
            this(format(template, arg1));
        }

        public InternalError(String template, Object arg1, Throwable cause) {
            this(format(template, arg1), cause);
        }

        public InternalError(String template, Object arg1, Object arg2) {
            this(format(template, arg1, arg2));
        }

        protected InternalError(String template, Object arg1, Object arg2, Throwable cause) {
            this(format(template, arg1, arg2), cause);
        }
    }

    public static class AlreadyExists extends SteveException {

        public AlreadyExists(String template, Object arg1) {
            super(format(template, arg1));
        }
    }

    public static class NotFound extends SteveException {

        public NotFound(String message) {
            super(message);
        }

        public NotFound(String template, Object arg1) {
            this(format(template, arg1));
        }
    }

    public static class BadRequest extends SteveException {
        public BadRequest(String message) {
            super(message);
        }

        public BadRequest(String template, Object arg1) {
            this(format(template, arg1));
        }
    }
}

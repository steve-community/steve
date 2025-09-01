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
    // Custom/extending classes
    // -------------------------------------------------------------------------

    public static class InternalError extends SteveException {

        public InternalError(String message) {
            super(message);
        }

        public InternalError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AlreadyExists extends SteveException {

        public AlreadyExists(String message) {
            super(message);
        }

        public AlreadyExists(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NotFound extends SteveException {

        public NotFound(String message) {
            super(message);
        }

        public NotFound(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class BadRequest extends SteveException {
        public BadRequest(String message) {
            super(message);
        }

        public BadRequest(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

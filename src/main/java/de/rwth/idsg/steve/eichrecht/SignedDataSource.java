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
package de.rwth.idsg.steve.eichrecht;

/**
 * How a meter value with format "SignedData" carries its signed data.
 */
public enum SignedDataSource {
    /**
     * The value is the OCMF string itself.
     */
    OCMF_PLAIN,

    /**
     * The value is base64 of the OCMF string.
     */
    OCMF_BASE64,

    /**
     * The value is a JSON object like OCPP 2.0.1 SignedMeterValueType, which wraps the signed data.
     */
    JSON_WRAPPER,

    /**
     * We could not recognize the format.
     */
    UNSUPPORTED
}

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
package de.rwth.idsg.steve.service.eichrecht.model;

/**
 * JSON object in the value of a signed meter value, like OCPP 2.x SignedMeterValueType:
 * {"signedMeterData": "...", "signingMethod": "...", "encodingMethod": "OCMF", "publicKey": "..."}
 *
 * @param signingMethod empty, if the signed meter data contains it (OCMF "SA"). Otherwise, it has to be set.
 *                      See Table 1 in https://openchargealliance.org/wp-content/uploads/2025/05/signed_meter_values-v10-1.pdf
 */
public record SignedMeterValueType(
    String signedMeterData,
    String signingMethod,
    String encodingMethod,
    String publicKey
) {
}

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

import de.rwth.idsg.steve.repository.dto.TransactionMeterValue;
import de.rwth.idsg.steve.service.eichrecht.model.input.OcmfRecord;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData;
import org.jetbrains.annotations.Nullable;

/**
 * A meter value with format "SignedData", as we read it. Internal: the input of the public key lookup and the
 * checks, see {@link SignedMeterData} for the result.
 *
 * @param ocmfRecord    null, if we could not read it (see parseError)
 * @param publicKey     sent with the value (OCPP SignedMeterValue JSON), not decoded
 * @param signingMethod sent with the value (OCPP SignedMeterValue JSON)
 */
public record SignedMeterValue(
    TransactionMeterValue meterValue,
    @Nullable OcmfRecord ocmfRecord,
    @Nullable String publicKey,
    @Nullable String signingMethod,
    @Nullable String parseError
) {
}

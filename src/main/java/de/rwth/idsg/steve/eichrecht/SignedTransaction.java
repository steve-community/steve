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

import de.rwth.idsg.steve.eichrecht.safe.Values;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import lombok.Builder;

import java.util.List;

public record SignedTransaction(
    int transactionId,
    List<SignedMeterValue> signedMeterValues,
    Values safeXmlValues
) {

    public record SignedMeterValue(
        TransactionDetails.MeterValues meterValue,
        SignedDataParseResult signedDataParseResult
    ) {
    }

    /**
     * Result of parsing the value of one meter value with format "SignedData".
     */
    @Builder
    public record SignedDataParseResult(
        SignedDataSource source,
        OcmfRecord ocmfRecord,
        String parseError,

        // These are only for JSON_WRAPPER source
        String signingMethod,
        String encodingMethod,
        String publicKey
    ) {
    }
}

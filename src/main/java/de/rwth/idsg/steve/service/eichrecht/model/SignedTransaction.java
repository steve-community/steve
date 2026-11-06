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

import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The signed meter values of a transaction.
 *
 * @param signedMeterValues all meter values with format "SignedData"
 * @param begin             the signed begin value, null if missing
 * @param end               the signed end value, null if missing or if the begin record carries both readings
 * @param publicKey         of the meter in hex or base64, null if unknown
 * @param checks            whether the signed data is valid and matches the transaction. Empty, if there is no
 *                          signed begin or end.
 */
public record SignedTransaction(
    int transactionId,
    List<SignedMeterValue> signedMeterValues,
    @Nullable SignedMeterValue begin,
    @Nullable SignedMeterValue end,
    @Nullable String publicKey,
    List<SignedDataCheck> checks
) {

    /**
     * @return false, if there is no file for the S.A.F.E. transparency software
     */
    public boolean hasBeginOrEnd() {
        return hasBegin() || hasEnd();
    }

    public boolean hasBegin() {
        return begin != null && begin.ocmfRecord != null;
    }

    public boolean hasEnd() {
        return end != null && end.ocmfRecord != null;
    }

    /**
     * @param ocmfRecord    null, if we could not read it (see parseError)
     * @param publicKey     sent with the value (OCPP SignedMeterValue JSON), not decoded
     * @param signingMethod sent with the value (OCPP SignedMeterValue JSON)
     */
    public record SignedMeterValue(
        TransactionDetails.MeterValues meterValue,
        @Nullable OcmfRecord ocmfRecord,
        @Nullable String publicKey,
        @Nullable String signingMethod,
        @Nullable String parseError
    ) {
    }
}

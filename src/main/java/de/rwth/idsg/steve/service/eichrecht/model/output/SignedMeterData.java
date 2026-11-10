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
package de.rwth.idsg.steve.service.eichrecht.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * The signed meter data of a transaction (Eichrecht), as shown in the web page and the REST API.
 */
@Schema(description = """
    The signed meter data of a transaction (Eichrecht), with the results of basic checks.
    For a full verification, use the S.A.F.E. XML of the transaction with the S.A.F.E. transparency software.
    Fields without a value are omitted.
    """)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignedMeterData(
    @Schema(description = "Number of meter values with format 'SignedData'")
    int receivedCount,

    @Schema(description = "Public key of the meter, as reported by the charging station. Omitted, if unknown.")
    @Nullable PublicKey publicKey,

    @Schema(description = "The signed begin value. Omitted, if not received.")
    @Nullable SignedReading begin,

    @Schema(description = "The signed end value. Omitted, if not received. If one signed record carries both readings, begin and end have the same 'signedData'.")
    @Nullable SignedReading end,

    @Schema(description = "Whether the signed data is valid and matches the transaction. Empty, if there is no signed begin or end.")
    List<SignedDataCheck> checks,

    @Schema(description = "The signed meter values that could not be read")
    List<UnreadableValue> unreadable
) {

    @Schema(description = "A signed reading of the meter, with the signed record that carries it")
    public record SignedReading(
        @Schema(description = "Timestamp of the meter value that carried the signed record")
        DateTime valueTimestamp,

        @Schema(description = "The signed reading, in 'unit'")
        BigDecimal value,

        @Schema(description = "Unit of the signed reading, as in the signed record (e.g. 'kWh')")
        String unit,

        @Schema(description = "Signing method with which the signature is checked (e.g. 'ECDSA-secp256r1-SHA256')")
        String signingMethod,

        @Schema(description = "Format of the signed record")
        Format format,

        @Schema(description = "The whole signed record in its format, as it goes into the S.A.F.E. XML")
        String signedData
    ) {
    }

    @Schema(description = "A signed meter value that could not be read")
    public record UnreadableValue(
        @Schema(description = "Timestamp of the meter value")
        DateTime valueTimestamp,

        @Schema(description = "Why it could not be read")
        String error
    ) {
    }

    @Schema(description = "Public key of the meter")
    public record PublicKey(
        @Schema(description = "Encoding of the value")
        Encoding encoding,

        @Schema(description = "The key in ASN.1 (X.509 SubjectPublicKeyInfo) format, encoded as given in 'encoding'")
        String value
    ) {
    }

    @Schema(description = "Format of a signed record")
    public enum Format {
        OCMF
    }

    @Schema(description = "Encoding of the public key")
    public enum Encoding {
        hex,
        base64
    }

    /**
     * @return false, if there is no file for the S.A.F.E. transparency software
     */
    @Schema(description = "Whether the S.A.F.E. XML of this transaction is available, i.e. there is a signed begin or end value.")
    @JsonProperty("safeXmlAvailable")
    public boolean safeXmlAvailable() {
        return begin != null || end != null;
    }
}

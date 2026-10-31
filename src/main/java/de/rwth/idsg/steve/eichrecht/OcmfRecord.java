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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * The fields of an OCMF record that we need.
 * https://github.com/SAFE-eV/OCMF-Open-Charge-Metering-Format/blob/master/OCMF-en.md
 *
 * @param rawInput   the whole record as received. Goes unchanged into the S.A.F.E. XML.
 * @param rawPayload the payload as received. The signature is over these bytes.
 */
public record OcmfRecord(
    String rawInput,
    String rawPayload,
    Payload payload,
    Signature signature
) {

    public record Payload(
        @JsonProperty("PG") String pagination,  // e.g. "T12" (transaction) or "F3" (fiscal)
        @JsonProperty("RD") List<Reading> readings
    ) {
    }

    public record Reading(
        @JsonProperty("TX") String transactionType,  // B(egin), E(nd), ...
        @JsonProperty("RV") BigDecimal value,
        @JsonProperty("RU") String unit              // e.g. "kWh"
    ) {
    }

    public record Signature(
        @JsonProperty("SA") String algorithm,  // default: ECDSA-secp256r1-SHA256
        @JsonProperty("SE") String encoding,   // default: hex
        @JsonProperty("SD") String data
    ) {
    }
}

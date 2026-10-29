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

import lombok.Builder;

import java.util.List;

/**
 * https://github.com/SAFE-eV/OCMF-Open-Charge-Metering-Format/blob/master/OCMF-en.md
 */
@Builder
public record OcmfRecord(
    String rawInput,
    String rawPayload,
    String rawSignature,
    PayloadSection payloadSection,
    SignatureSection signatureSection
) {

    @Builder
    public record PayloadSection(
        // Table 1: General Information Fields
        String formatVersion,  // FV
        String gatewayId,      // GI
        String gatewaySerial,  // GS
        String gatewayVersion, // GV

        // Table 2: Pagination Field
        String pagination,  // PG, e.g. "T12" (transaction) or "F3" (fiscal)

        // Table 3: Fields for Identification of the Meter
        String meterVendor,     // MV
        String meterModel,      // MM
        String meterSerial,     // MS
        String meterFirmware,   // MF

        // Table 4: Fields for User Assignment
        String identificationStatus,        // IS
        String identificationLevel,         // IL
        List<String> identificationFlags,   // IF
        String identificationType,          // IT
        String identificationData,          // ID
        String tariffText,                  // TT

        // Table 5: Fields for EVSE Metrologic Parameters
        String chargeControllerFirmwareVersion, // CF
        LossCompensation lossCompensation,      // LC

        // Table 6: Fields for Identification of the Charge Point
        String chargePointIdType,     // CT
        String chargePointId,         // CI

        List<Reading> readings    // RD
    ) {
    }

    @Builder
    public record Reading(
        String time,              // TM, e.g. "2018-07-24T13:22:04,000+0200 S"
        String transactionType,   // TX: B(egin), C, X, E(nd), L, R, A, P, S, T
        String value,             // RV
        String identifier,        // RI, e.g. OBIS code "1-b:1.8.0"
        String unit,              // RU, e.g. "kWh"
        String currentType,       // RT, "AC" or "DC"
        String cumulatedLoss,     // CL
        String errorFlags,        // EF
        String status             // ST, e.g. "G" (good)
    ) {
    }

    @Builder
    public record LossCompensation(
        String naming,              // LN
        String identification,      // LI
        String cableResistance,     // LR
        String cableResistanceUnit  // LU
    ) {
    }

    @Builder
    public record SignatureSection(
        String signatureAlgorithm,    // SA (default per spec: ECDSA-secp256r1-SHA256)
        String signatureEncoding,     // SE (default per spec: hex)
        String signatureMimeType,     // SM (default per spec: application/x-der)
        String signatureData          // SD
    ) {
    }
}

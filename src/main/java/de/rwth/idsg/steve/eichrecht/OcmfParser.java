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

import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;

public class OcmfParser {

    public static final String OCMF_PREFIX = "OCMF|";

    // Keep numbers exactly as sent (e.g. "0.0300" must not become "0.03")
    static final ObjectMapper MAPPER = JsonMapper.builder()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        .build();

    public OcmfRecord parse(String rawValue) {
        String trimmedRawValue = rawValue.trim();
        if (!trimmedRawValue.startsWith(OCMF_PREFIX)) {
            throw new IllegalArgumentException("Does not start with '" + OCMF_PREFIX + "'");
        }

        // The signature section never contains '|', so the last '|' separates payload and signature
        int lastPipe = trimmedRawValue.lastIndexOf('|');
        if (lastPipe < OCMF_PREFIX.length()) {
            throw new IllegalArgumentException("Missing signature section");
        }

        var rawPayload = trimmedRawValue.substring(OCMF_PREFIX.length(), lastPipe);
        var rawSignature = trimmedRawValue.substring(lastPipe + 1);

        var payload = readObject(rawPayload, "payload");
        var payloadSection = OcmfRecord.PayloadSection.builder()
            .formatVersion(toString(payload, "FV"))
            .gatewayId(toString(payload, "GI"))
            .gatewaySerial(toString(payload, "GS"))
            .gatewayVersion(toString(payload, "GV"))
            .pagination(toString(payload, "PG"))
            .meterVendor(toString(payload, "MV"))
            .meterModel(toString(payload, "MM"))
            .meterSerial(toString(payload, "MS"))
            .meterFirmware(toString(payload, "MF"))
            .identificationStatus(toString(payload, "IS"))
            .identificationLevel(toString(payload, "IL"))
            .identificationFlags(toListOfStrings(payload.get("IF")))
            .identificationType(toString(payload, "IT"))
            .identificationData(toString(payload, "ID"))
            .tariffText(toString(payload, "TT"))
            .chargeControllerFirmwareVersion(toString(payload, "CF"))
            .lossCompensation(toLossCompensation(payload.get("LC")))
            .chargePointIdType(toString(payload, "CT"))
            .chargePointId(toString(payload, "CI"))
            .readings(readings(payload.get("RD")))
            .build();

        var signature = readObject(rawSignature, "signature");
        var signatureSection = OcmfRecord.SignatureSection.builder()
            .signatureAlgorithm(toString(signature, "SA"))
            .signatureEncoding(toString(signature, "SE"))
            .signatureMimeType(toString(signature, "SM"))
            .signatureData(toString(signature, "SD"))
            .build();

        return OcmfRecord.builder()
            .rawInput(trimmedRawValue)
            .rawPayload(rawPayload)
            .rawSignature(rawSignature)
            .payloadSection(payloadSection)
            .signatureSection(signatureSection)
            .build();
    }

    @Nullable
    private static OcmfRecord.LossCompensation toLossCompensation(JsonNode node) {
        if (node == null || node.isNull() || !node.isObject()) {
            return null;
        }

        return OcmfRecord.LossCompensation.builder()
            .naming(toString(node, "LN"))
            .identification(toString(node, "LI"))
            .cableResistance(toString(node, "LR"))
            .cableResistanceUnit(toString(node, "LU"))
            .build();
    }

    private static List<OcmfRecord.Reading> readings(JsonNode rd) {
        if (rd == null || rd.isNull() || !rd.isArray()) {
            return Collections.emptyList();
        }

        return rd.values()
            .stream()
            .map(r -> OcmfRecord.Reading.builder()
                .time(toString(r, "TM"))
                .transactionType(toString(r, "TX"))
                .value(toString(r, "RV"))
                .identifier(toString(r, "RI"))
                .unit(toString(r, "RU"))
                .currentType(toString(r, "RT"))
                .cumulatedLoss(toString(r, "CL"))
                .errorFlags(toString(r, "EF"))
                .status(toString(r, "ST"))
                .build())
            .toList();
    }

    private static List<String> toListOfStrings(JsonNode node) {
        if (node == null || node.isNull() || !node.isArray()) {
            return Collections.emptyList();
        }

        return node.values()
            .stream()
            .map(JsonNode::asString)
            .toList();
    }

    static JsonNode readObject(String json, String section) {
        JsonNode node = MAPPER.readTree(json);
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("The " + section + " section is not a JSON object");
        }
        return node;
    }

    @Nullable
    static String toString(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asString();
    }

}

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
package de.rwth.idsg.steve.service.eichrecht;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static de.rwth.idsg.steve.service.eichrecht.OcmfUtils.PREFIX;

/**
 * Parses OCMF records and reads values from them.
 */
public final class OcmfParser {

    static final ObjectMapper MAPPER = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    private OcmfParser() {
    }

    public static OcmfRecord parse(String rawValue) {
        String rawInput = rawValue.trim();
        if (!rawInput.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Does not start with '" + PREFIX + "'");
        }

        // The signature section never contains '|', so the last '|' separates payload and signature
        int lastPipe = rawInput.lastIndexOf('|');
        if (lastPipe < PREFIX.length()) {
            throw new IllegalArgumentException("Missing signature section");
        }

        String rawPayload = rawInput.substring(PREFIX.length(), lastPipe);
        String rawSignature = rawInput.substring(lastPipe + 1);

        return new OcmfRecord(
            rawInput,
            rawPayload,
            MAPPER.readValue(rawPayload, OcmfRecord.Payload.class),
            MAPPER.readValue(rawSignature, OcmfRecord.Signature.class)
        );
    }
}

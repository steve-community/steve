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

import de.rwth.idsg.steve.service.eichrecht.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.MeterPublicKeySources;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Finds the public key of the meter, with which the signed values of a transaction can be verified.
 * See the OCA application note "Signed Meter Values in OCPP" (v1.0).
 */
@Service
public class MeterPublicKeyService {

    private static final String CONFIGURATION_KEY = "MeterPublicKey";
    private static final Pattern HEX = Pattern.compile("[0-9a-fA-F]+");

    /**
     * @param publicKey as returned by {@link #find}
     * @return true for hex, false for base64
     */
    public static boolean isHex(String publicKey) {
        return HEX.matcher(publicKey).matches();
    }

    /**
     * A transaction has one meter, so one key. Either sent with a signed value (some stations send it only once
     * per transaction), or from the configuration key of the charge point.
     *
     * @return the key in hex or base64, or null, if unknown
     */
    @Nullable
    public String find(List<SignedMeterValue> signedValues, @Nullable MeterPublicKeySources sources) {
        for (var smv : signedValues) {
            String key = unwrap(smv.publicKey());
            if (key != null) {
                return key;
            }
        }

        if (sources == null || sources.ocppConfiguration() == null) {
            return null;
        }

        // "MeterPublicKey1" for connector 1 according to the note. Some stations have one for all connectors.
        List<String> names = List.of(
            CONFIGURATION_KEY + sources.connectorId(),
            CONFIGURATION_KEY + "[" + sources.connectorId() + "]",
            CONFIGURATION_KEY
        );
        for (String name : names) {
            String key = unwrap(sources.ocppConfiguration().path(name).asString());
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    /**
     * The note specifies the value as Base64 of "oca:encoding:content-type:key", e.g. "oca:base16:asn1:3059...".
     * Otherwise, we take the value as the key itself.
     */
    @Nullable
    static String unwrap(@Nullable String raw) {
        String value = StringUtils.deleteWhitespace(raw);
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(value), StandardCharsets.US_ASCII);
        } catch (IllegalArgumentException e) {
            return value;
        }

        if (!decoded.startsWith("oca:")) {
            return value;
        }

        String[] parts = decoded.split(":", 4);
        if (parts.length != 4) {
            return null;
        }
        return "base16".equals(parts[1])
            ? parts[3].replaceAll("0[xX]|[^0-9a-fA-F]", "") // the note says to ignore these
            : StringUtils.deleteWhitespace(parts[3]);
    }
}

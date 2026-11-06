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

import de.rwth.idsg.steve.service.eichrecht.model.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.MeterPublicKeySources;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
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
     * Bouncy Castle, because the JDK does not support all curves of OCMF (e.g. brainpool)
     */
    static final Provider PROVIDER = new BouncyCastleProvider();

    /**
     * @param publicKey as returned by {@link #find}
     * @return true for hex, false for base64
     */
    public static boolean isHex(String publicKey) {
        return HEX.matcher(publicKey).matches();
    }

    /**
     * @param publicKey as returned by {@link #find}: ASN.1 (X.509 SubjectPublicKeyInfo) in hex or base64
     * @return null, if it is not an EC public key
     */
    @Nullable
    public static ECPublicKey decode(String publicKey) {
        try {
            var encoded = isHex(publicKey)
                ? HexFormat.of().parseHex(publicKey)
                : Base64.getDecoder().decode(publicKey);

            var key = KeyFactory.getInstance("EC", PROVIDER).generatePublic(new X509EncodedKeySpec(encoded));

            return (key instanceof ECPublicKey ecKey)
                ? ecKey
                : null;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * A transaction has one meter, so one key. Either sent with a signed value (some stations send it only once
     * per transaction), or from the configuration key of the charge point. The first one that we can read wins.
     *
     * @return the key in hex or base64. The first one, if we cannot read any. Null, if unknown.
     */
    @Nullable
    public String find(List<SignedMeterValue> signedValues, @Nullable MeterPublicKeySources sources) {
        var candidates = new ArrayList<String>();
        signedValues.forEach(smv -> candidates.add(smv.publicKey()));

        if (sources != null && sources.ocppConfiguration() != null) {
            // "MeterPublicKey1" for connector 1 according to the note. Some stations have one for all connectors.
            var names = List.of(
                CONFIGURATION_KEY + sources.connectorId(),
                CONFIGURATION_KEY + "[" + sources.connectorId() + "]",
                CONFIGURATION_KEY
            );
            names.forEach(name -> candidates.add(sources.ocppConfiguration().path(name).asString()));
        }

        var keys = candidates.stream()
            .map(MeterPublicKeyService::unwrap)
            .filter(Objects::nonNull)
            .toList();

        return keys.stream()
            .filter(key -> decode(key) != null)
            .findFirst()
            .orElse(keys.isEmpty() ? null : keys.getFirst());
    }

    /**
     * The note specifies the value as Base64 of "oca:encoding:content-type:key", e.g. "oca:base16:asn1:3059...".
     * Otherwise, we take the value as the key itself.
     */
    @Nullable
    static String unwrap(@Nullable String raw) {
        var value = StringUtils.deleteWhitespace(raw);
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

        var parts = decoded.split(":", 4);
        if (parts.length != 4) {
            return null;
        }
        return "base16".equals(parts[1])
            ? parts[3].replaceAll("0[xX]|[^0-9a-fA-F]", "") // the note says to ignore these
            : StringUtils.deleteWhitespace(parts[3]);
    }
}

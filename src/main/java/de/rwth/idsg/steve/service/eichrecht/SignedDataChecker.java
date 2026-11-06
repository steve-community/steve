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

import de.rwth.idsg.steve.service.eichrecht.SignedDataCheck.Result;
import de.rwth.idsg.steve.service.eichrecht.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Checks that the signed data of a transaction is valid and matches the transaction.
 */
@Slf4j
public final class SignedDataChecker {

    /**
     * Bouncy Castle, because the JDK does not support all curves of OCMF (e.g. brainpool)
     */
    private static final Provider PROVIDER = new BouncyCastleProvider();

    private static final String DEFAULT_ALGORITHM = "ECDSA-secp256r1-SHA256";

    /**
     * The supported signing methods, with the name of their curve in Bouncy Castle. All use SHA-256.
     * From "Table 12. signingMethod values" in
     * https://openchargealliance.org/wp-content/uploads/2025/05/signed_meter_values-v10-1.pdf
     */
    private static final Map<String, String> CURVES = Map.of(
        "ECDSA-secp192k1-SHA256", "secp192k1",
        "ECDSA-secp256k1-SHA256", "secp256k1",
        "ECDSA-secp192r1-SHA256", "secp192r1",
        "ECDSA-secp256r1-SHA256", "secp256r1",
        "ECDSA-brainpool256r1-SHA256", "brainpoolP256r1",
        "ECDSA-secp384r1-SHA256", "secp384r1",
        "ECDSA-brainpool384r1-SHA256", "brainpoolP384r1"
    );

    private SignedDataChecker() {
    }

    /**
     * @param begin     the signed begin value, null if missing
     * @param end       the signed end value, null if missing or if the begin record carries both readings
     * @param publicKey of the meter in hex or base64, null if unknown
     * @return empty, if there is no signed begin or end
     */
    public static List<SignedDataCheck> check(@Nullable SignedMeterValue begin,
                                              @Nullable SignedMeterValue end,
                                              @Nullable String publicKey,
                                              Transaction transaction) {
        if (begin == null && end == null) {
            return List.of();
        }

        // The begin record can carry both readings
        var endValue = (end == null && OcmfUtils.endReading(begin.ocmfRecord()) != null) ? begin : end;

        var checks = new ArrayList<SignedDataCheck>();

        if (begin == null) {
            checks.add(new SignedDataCheck("Signed begin value", Result.Failed, "Not received"));
        } else {
            checks.add(signature("Signature of the begin value", begin, publicKey));
            checks.add(value(
                "Begin value equals the start value of the transaction",
                OcmfUtils.beginReading(begin.ocmfRecord()),
                transaction.getStartValue()
            ));
        }

        if (endValue == null) {
            var stopped = transaction.getStopValue() != null;
            var result = stopped ? Result.Failed : Result.NotChecked;
            var note = stopped ? "Not received" : "The transaction is not stopped yet";
            checks.add(new SignedDataCheck("Signed end value", result, note));
        } else {
            if (endValue != begin) {
                checks.add(signature("Signature of the end value", endValue, publicKey));
            }
            checks.add(value(
                "End value equals the stop value of the transaction",
                OcmfUtils.endReading(endValue.ocmfRecord()),
                transaction.getStopValue()
            ));
        }

        return checks;
    }

    private static SignedDataCheck value(String description,
                                         OcmfRecord.Reading reading,
                                         @Nullable String transactionValue) {
        var signedWh = OcmfUtils.valueInWh(reading);
        if (signedWh == null) {
            return new SignedDataCheck(description, Result.NotChecked, "Unknown unit " + reading.unit());
        }
        if (transactionValue == null) {
            return new SignedDataCheck(description, Result.NotChecked, "The transaction has no such value");
        }

        BigDecimal transactionWh;
        try {
            transactionWh = new BigDecimal(transactionValue.trim());
        } catch (NumberFormatException e) {
            return new SignedDataCheck(description, Result.NotChecked, "The transaction value is not a number");
        }

        var note = "Signed: %s %s, transaction: %s Wh"
            .formatted(
                reading.value().toPlainString(),
                reading.unit(),
                transactionValue
            );

        // The transaction has whole Wh, the meter can have more decimals
        var equal = signedWh.subtract(transactionWh).abs().compareTo(BigDecimal.ONE) < 0;
        var result = equal ? Result.Passed : Result.Failed;
        return new SignedDataCheck(description, result, note);
    }

    /**
     * Same as the S.A.F.E. transparency software: ECDSA with SHA-256 over the payload as received
     */
    private static SignedDataCheck signature(String description,
                                             SignedMeterValue value,
                                             @Nullable String publicKey) {
        if (publicKey == null) {
            return new SignedDataCheck(description, Result.NotChecked, "The public key of the meter is unknown");
        }

        var record = value.ocmfRecord();
        var signature = record.signature();
        var algorithm = signingMethod(value);
        var curve = CURVES.get(algorithm);
        if (curve == null) {
            return new SignedDataCheck(description, Result.NotChecked, "Unsupported algorithm " + algorithm);
        }

        PublicKey key;
        try {
            var encoded = MeterPublicKeyService.isHex(publicKey)
                ? HexFormat.of().parseHex(publicKey)
                : Base64.getDecoder().decode(publicKey);

            key = KeyFactory.getInstance("EC", PROVIDER).generatePublic(new X509EncodedKeySpec(encoded));

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            String errorMsg = "Cannot read the public key of the meter";
            log.error(errorMsg, e);
            return new SignedDataCheck(description, Result.NotChecked, errorMsg);
        }

        // The key carries its own curve, which we verify with. S.A.F.E. instead uses the curve of the
        // algorithm. Both have to be the same, so that S.A.F.E. comes to the same result.
        var expected = ECNamedCurveTable.getParameterSpec(curve);
        var actual = ((ECPublicKey) key).getParameters();
        if (!expected.getCurve().equals(actual.getCurve()) || !expected.getG().equals(actual.getG())) {
            var note = "The public key of the meter does not match the algorithm " + algorithm;
            return new SignedDataCheck(description, Result.Failed, note);
        }

        try {
            var signatureData = "base64".equalsIgnoreCase(signature.encoding())
                ? Base64.getDecoder().decode(signature.data())
                : HexFormat.of().parseHex(signature.data());

            var verifier = Signature.getInstance("SHA256withECDSA", PROVIDER);
            verifier.initVerify(key);
            verifier.update(record.rawPayload().getBytes(StandardCharsets.UTF_8));

            // S.A.F.E. only gets the OCMF record, so it can only know the default
            var passedNote = StringUtils.isBlank(signature.algorithm()) && !DEFAULT_ALGORITHM.equals(algorithm)
                ? "Signing method from the charging station, not in the OCMF record: S.A.F.E. uses " + DEFAULT_ALGORITHM
                : null;

            return verifier.verify(signatureData)
                ? new SignedDataCheck(description, Result.Passed, passedNote)
                : new SignedDataCheck(description, Result.Failed, "The signature does not match the data");

        } catch (GeneralSecurityException | RuntimeException e) {
            var errorMsg = "Malformed signature";
            log.error(errorMsg, e);
            return new SignedDataCheck(description, Result.Failed, errorMsg);
        }
    }

    /**
     * 1. "SA" of the OCMF record, if the signed meter data contains it.
     * 2. Otherwise, signingMethod of the OCPP SignedMeterValue JSON (see Table 1 of the OCA application note).
     * 3. Otherwise, the default of OCMF.
     */
    private static String signingMethod(SignedMeterValue value) {
        var fromOcmf = value.ocmfRecord().signature().algorithm();
        if (StringUtils.isNotBlank(fromOcmf)) {
            return fromOcmf;
        }
        if (StringUtils.isNotBlank(value.signingMethod())) {
            return value.signingMethod().trim();
        }
        return DEFAULT_ALGORITHM;
    }
}

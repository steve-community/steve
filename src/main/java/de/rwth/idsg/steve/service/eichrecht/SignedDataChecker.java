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

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.service.eichrecht.model.input.OcmfRecord;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedDataCheck;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedDataCheck.Result;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedDataCheck.Type;
import de.rwth.idsg.steve.service.eichrecht.model.SignedMeterValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.service.eichrecht.MeterPublicKeyExtractor.PROVIDER;

/**
 * Checks that the signed data of a transaction is valid and matches the transaction.
 */
@Slf4j
public final class SignedDataChecker {

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

    static final String KEY_NOTE =
        "Valid for the public key reported by the charging station. Compare it with the key on the meter.";

    private SignedDataChecker() {
    }

    /**
     * @param begin     the signed begin value, null if missing
     * @param end       the signed end value, null if missing. Has the same record as begin, if that carries both readings.
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

        var checks = new ArrayList<SignedDataCheck>();

        if (begin == null) {
            checks.add(new SignedDataCheck(Type.BeginReceived, "Signed begin value", Result.Failed, "Not received"));
        } else {
            checks.add(signature(Type.BeginSignature, "Signature of the begin value", begin, publicKey));
            checks.add(value(
                Type.BeginValue,
                "Begin value equals the start value of the transaction",
                OcmfUtils.beginReading(begin.ocmfRecord()),
                transaction.getStartValue()
            ));
        }

        if (end == null) {
            var stopped = transaction.getStopValue() != null;
            var result = stopped ? Result.Failed : Result.NotChecked;
            var note = stopped ? "Not received" : "The transaction is not stopped yet";
            checks.add(new SignedDataCheck(Type.EndReceived, "Signed end value", result, note));
        } else {
            // One record with both readings: its signature is already checked
            if (begin == null || !begin.ocmfRecord().rawInput().equals(end.ocmfRecord().rawInput())) {
                checks.add(signature(Type.EndSignature, "Signature of the end value", end, publicKey));
            }
            checks.add(value(
                Type.EndValue,
                "End value equals the stop value of the transaction",
                OcmfUtils.endReading(end.ocmfRecord()),
                transaction.getStopValue()
            ));
        }

        return checks;
    }

    private static SignedDataCheck value(Type type,
                                         String description,
                                         OcmfRecord.Reading reading,
                                         @Nullable String transactionValue) {
        var signedWh = OcmfUtils.valueInWh(reading);
        if (signedWh == null) {
            return new SignedDataCheck(type, description, Result.NotChecked, "Unknown unit " + reading.unit());
        }
        if (transactionValue == null) {
            return new SignedDataCheck(type, description, Result.NotChecked, "The transaction has no such value");
        }

        BigDecimal transactionWh;
        try {
            transactionWh = new BigDecimal(transactionValue.trim());
        } catch (NumberFormatException e) {
            return new SignedDataCheck(type, description, Result.NotChecked, "The transaction value is not a number");
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
        return new SignedDataCheck(type, description, result, note);
    }

    /**
     * Same as the S.A.F.E. transparency software: ECDSA with SHA-256 over the payload as received
     */
    private static SignedDataCheck signature(Type type,
                                             String description,
                                             SignedMeterValue value,
                                             @Nullable String publicKey) {
        if (publicKey == null) {
            return new SignedDataCheck(type, description, Result.NotChecked, "The public key of the meter is unknown");
        }

        var record = value.ocmfRecord();
        var signature = record.signature();
        var algorithm = signingMethod(value);
        var curve = CURVES.get(algorithm);
        if (curve == null) {
            return new SignedDataCheck(type, description, Result.NotChecked, "Unsupported algorithm " + algorithm);
        }

        var key = MeterPublicKeyExtractor.decode(publicKey);
        if (key == null) {
            return new SignedDataCheck(type, description, Result.NotChecked, "Cannot read the public key of the meter");
        }

        // The key carries its own curve, which we verify with. S.A.F.E. instead uses the curve of the
        // algorithm. Both have to be the same, so that S.A.F.E. comes to the same result.
        var expected = ECNamedCurveTable.getParameterSpec(curve);
        var actual = key.getParameters();
        if (!expected.getCurve().equals(actual.getCurve()) || !expected.getG().equals(actual.getG())) {
            var note = "The public key of the meter does not match the algorithm " + algorithm;
            return new SignedDataCheck(type, description, Result.Failed, note);
        }

        try {
            var signatureData = "base64".equalsIgnoreCase(signature.encoding())
                ? Base64.getDecoder().decode(signature.data())
                : HexFormat.of().parseHex(signature.data());

            var verifier = Signature.getInstance("SHA256withECDSA", PROVIDER);
            verifier.initVerify(key);
            verifier.update(record.rawPayload().getBytes(StandardCharsets.UTF_8));

            // The key always comes from the charging station, whether sent with the value or from its
            // configuration. Only the key on the meter (or of the Bundesnetzagentur) is trustworthy.
            var passedNote = KEY_NOTE;

            // S.A.F.E. only gets the OCMF record, so it can only know the default
            if (StringUtils.isBlank(signature.algorithm()) && !DEFAULT_ALGORITHM.equals(algorithm)) {
                passedNote += " Signing method from the charging station, not in the OCMF record: S.A.F.E. uses " + DEFAULT_ALGORITHM + ".";
            }

            return verifier.verify(signatureData)
                ? new SignedDataCheck(type, description, Result.Passed, passedNote)
                : new SignedDataCheck(type, description, Result.Failed, "The signature does not match the data");

        } catch (GeneralSecurityException | RuntimeException e) {
            var errorMsg = "Malformed signature";
            log.error(errorMsg, e);
            return new SignedDataCheck(type, description, Result.Failed, errorMsg);
        }
    }

    /**
     * 1. "SA" of the OCMF record, if the signed meter data contains it.
     * 2. Otherwise, signingMethod of the OCPP SignedMeterValue JSON (see Table 1 of the OCA application note).
     * 3. Otherwise, the default of OCMF.
     */
    static String signingMethod(SignedMeterValue value) {
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

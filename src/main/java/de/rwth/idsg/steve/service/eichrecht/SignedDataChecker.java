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
import de.rwth.idsg.steve.repository.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
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

    private SignedDataChecker() {
    }

    /**
     * @param begin     the signed begin record, null if missing
     * @param end       the signed end record, null if missing or if the begin record carries both readings
     * @param publicKey of the meter in hex or base64, null if unknown
     * @return empty, if there is no signed begin or end
     */
    public static List<SignedDataCheck> check(@Nullable OcmfRecord begin,
                                              @Nullable OcmfRecord end,
                                              @Nullable String publicKey,
                                              Transaction transaction) {
        if (begin == null && end == null) {
            return List.of();
        }

        // The begin record can carry both readings
        var endRecord = (end == null && OcmfUtils.endReading(begin) != null) ? begin : end;

        List<SignedDataCheck> checks = new ArrayList<>();

        if (begin == null) {
            checks.add(new SignedDataCheck("Signed begin value", Result.Failed, "Not received"));
        } else {
            checks.add(signature("Signature of the begin value", begin, publicKey));
            checks.add(value(
                "Begin value equals the start value of the transaction",
                OcmfUtils.beginReading(begin),
                transaction.getStartValue()
            ));
        }

        if (endRecord == null) {
            var stopped = transaction.getStopValue() != null;
            var result = stopped ? Result.Failed : Result.NotChecked;
            var note = stopped ? "Not received" : "The transaction is not stopped yet";
            checks.add(new SignedDataCheck("Signed end value", result, note));
        } else {
            if (endRecord != begin) {
                checks.add(signature("Signature of the end value", endRecord, publicKey));
            }
            checks.add(value(
                "End value equals the stop value of the transaction",
                OcmfUtils.endReading(endRecord),
                transaction.getStopValue()
            ));
        }

        return checks;
    }

    private static SignedDataCheck value(String description,
                                         OcmfRecord.Reading reading,
                                         @Nullable String transactionValue) {
        BigDecimal signedWh = OcmfUtils.valueInWh(reading);
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

        String note = "Signed: %s %s, transaction: %s Wh"
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
                                             OcmfRecord record,
                                             @Nullable String publicKey) {
        if (publicKey == null) {
            return new SignedDataCheck(description, Result.NotChecked, "The public key of the meter is unknown");
        }

        var signature = record.signature();
        var algorithm = signature.algorithm() == null ? DEFAULT_ALGORITHM : signature.algorithm();
        if (!algorithm.startsWith("ECDSA-") || !algorithm.endsWith("-SHA256")) {
            return new SignedDataCheck(description, Result.NotChecked, "Unsupported algorithm " + algorithm);
        }

        PublicKey key;
        try {
            byte[] encoded = MeterPublicKeyService.isHex(publicKey)
                ? HexFormat.of().parseHex(publicKey)
                : Base64.getDecoder().decode(publicKey);

            key = KeyFactory.getInstance("EC", PROVIDER).generatePublic(new X509EncodedKeySpec(encoded));

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            String errorMsg = "Cannot read the public key of the meter";
            log.error(errorMsg, e);
            return new SignedDataCheck(description, Result.NotChecked, errorMsg);
        }

        try {
            byte[] signatureData = "base64".equalsIgnoreCase(signature.encoding())
                ? Base64.getDecoder().decode(signature.data())
                : HexFormat.of().parseHex(signature.data());

            var verifier = Signature.getInstance("SHA256withECDSA", PROVIDER);
            verifier.initVerify(key);
            verifier.update(record.rawPayload().getBytes(StandardCharsets.UTF_8));

            return verifier.verify(signatureData)
                ? new SignedDataCheck(description, Result.Passed, null)
                : new SignedDataCheck(description, Result.Failed, "The signature does not match the data");

        } catch (GeneralSecurityException | RuntimeException e) {
            String errorMsg = "Malformed signature";
            log.error(errorMsg, e);
            return new SignedDataCheck(description, Result.Failed, errorMsg);
        }
    }
}

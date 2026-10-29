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
package de.rwth.idsg.steve.eichrecht.safe;

import de.rwth.idsg.steve.eichrecht.OcmfRecord;
import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedMeterValue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Maps the signed begin and end records of a transaction to the S.A.F.E. XML format.
 * The transparency software pairs the two by their transactionId and shows the energy between them.
 * <p>
 * If there is only one record (it carries both readings, or begin or end is missing), we leave out the
 * transactionId. Otherwise, the transparency software tries to verify it as a transaction, which needs exactly
 * one begin and one end value, and fails. Without it, the record is verified on its own. If it carries both
 * readings, the software still recognizes it as a complete transaction.
 */
@Slf4j
public final class ValuesMapper {

    private static final String CONTEXT_BEGIN = "Transaction.Begin";
    private static final String CONTEXT_END = "Transaction.End";

    private static final Pattern HEX = Pattern.compile("[0-9a-fA-F]+");

    private static final Set<String> BEGIN_TYPES = Set.of("B");

    /**
     * Besides E (end), a transaction can also stop with L (stopped locally), R (stopped remotely),
     * A (aborted) or P (power failure). Same as the S.A.F.E. transparency software.
     */
    private static final Set<String> END_TYPES = Set.of("E", "L", "R", "A", "P");

    private ValuesMapper() {
    }

    /**
     * @param publicKey of the meter in hex or base64, null if unknown
     */
    public static Values map(int transactionId,
                             List<SignedMeterValue> signedMeterValues,
                             @Nullable String publicKey) {
        var ocmfValues = signedMeterValues
            .stream()
            .filter(Objects::nonNull)
            .filter(smv -> smv.signedDataParseResult().ocmfRecord() != null)
            .filter(smv -> isTransactionPagination(payload(smv)))
            .toList();

        var begin = ocmfValues.stream()
            .filter(smv -> containsTypes(payload(smv), BEGIN_TYPES))
            .findFirst()
            .orElse(null);

        var end = ocmfValues.stream()
            .filter(smv -> containsTypes(payload(smv), END_TYPES))
            .reduce((first, second) -> second)
            .orElse(null);

        // One record can carry both readings. Then it is enough to include it once.
        if (begin != null && end != null && sameRecord(begin, end)) {
            end = null;
        }

        Integer transactionIdInXml = (begin != null && end != null) ? transactionId : null;

        List<Values.Value> values = new ArrayList<>();
        toValueAndCollect(CONTEXT_BEGIN, begin, transactionIdInXml, publicKey, values);
        toValueAndCollect(CONTEXT_END, end, transactionIdInXml, publicKey, values);

        // Debug level, since the details page checks every transaction, and most have no signed data
        if (values.isEmpty()) {
            log.debug(
                "Transaction {} has no signed begin or end record. Creating empty S.A.F.E. payload",
                transactionId
            );
        }

        return new Values().setValues(values);
    }

    /**
     * Pagination "T" marks transaction records, "F" fiscal records. Like the S.A.F.E. transparency software,
     * we only treat "T" records as transaction data.
     */
    private static boolean isTransactionPagination(OcmfRecord.PayloadSection payloadSection) {
        var pagination = payloadSection.pagination();
        return pagination != null && pagination.length() > 1 && pagination.startsWith("T");
    }

    private static boolean containsTypes(OcmfRecord.PayloadSection payloadSection, Set<String> types) {
        var readings = payloadSection.readings();
        if (readings == null) {
            return false;
        }

        for (var reading : readings) {
            var transactionType = reading.transactionType();
            if (transactionType != null && types.contains(transactionType.trim())) {
                return true;
            }
        }
        return false;
    }

    private static void toValueAndCollect(String context,
                                          @Nullable SignedMeterValue smv,
                                          @Nullable Integer transactionId,
                                          @Nullable String publicKey,
                                          List<Values.Value> collector) {
        if (smv == null) {
            return;
        }

        var value = new Values.Value()
            .setContext(context)
            .setTransactionId(transactionId)
            .setPublicKey(toPublicKey(publicKey))
            .setSignedData(toSignedData(smv));

        collector.add(value);
    }

    /**
     * The public key is optional in S.A.F.E. XML. If we do not know it, we leave it out, because the transparency
     * software rejects an empty publicKey element. The user can then enter the key in the software.
     */
    @Nullable
    private static Values.PublicKey toPublicKey(@Nullable String publicKey) {
        if (publicKey == null) {
            return null;
        }

        return new Values.PublicKey()
            .setEncoding(HEX.matcher(publicKey).matches() ? Values.EncodingType.hex : Values.EncodingType.base64)
            .setValue(publicKey);
    }

    private static Values.SignedData toSignedData(SignedMeterValue smv) {
        return new Values.SignedData()
            .setFormat("OCMF") // currently we only support this
            .setEncoding(Values.EncodingType.plain)
            .setValue(smv.signedDataParseResult().ocmfRecord().rawInput());
    }

    private static OcmfRecord.PayloadSection payload(SignedMeterValue smv) {
        return smv.signedDataParseResult().ocmfRecord().payloadSection();
    }

    private static boolean sameRecord(SignedMeterValue a, SignedMeterValue b) {
        return a.signedDataParseResult().ocmfRecord().rawInput()
            .equals(b.signedDataParseResult().ocmfRecord().rawInput());
    }
}

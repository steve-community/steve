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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.MeterPublicKeySources;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.service.eichrecht.model.SignedMeterValueType;
import de.rwth.idsg.steve.service.eichrecht.model.SignedTransaction;
import de.rwth.idsg.steve.service.eichrecht.model.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.service.eichrecht.safe.ValuesXmlWriter;
import ocpp.cs._2015._10.ValueFormat;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;

import jakarta.xml.bind.JAXBException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static de.rwth.idsg.steve.service.eichrecht.OcmfUtils.PREFIX;

@Service
public class EichrechtService {

    private final ValuesXmlWriter valuesXmlWriter = new ValuesXmlWriter();

    public String createXML(SignedTransaction signed) {
        try {
            return valuesXmlWriter.createXML(signed);
        } catch (JAXBException e) {
            throw new SteveException("Could not create the S.A.F.E. XML of transaction %s", signed.transactionId(), e);
        }
    }

    public SignedTransaction getSignedTransaction(Transaction transaction,
                                                  MeterPublicKeySources meterPublicKeySources,
                                                  List<TransactionDetails.MeterValues> values) {
        // Get all meter values with format "SignedData" of one transaction
        var signedValues = values.stream()
            .filter(it -> ValueFormat.SIGNED_DATA.value().equals(it.getFormat()))
            .map(EichrechtService::parse)
            .toList();

        var records = signedValues.stream()
            .filter(v -> v.ocmfRecord() != null)
            .filter(v -> OcmfUtils.isTransactionRecord(v.ocmfRecord()))
            .toList();

        var begin = records.stream()
            .filter(v -> OcmfUtils.beginReading(v.ocmfRecord()) != null)
            .findFirst()
            .orElse(null);

        var end = records.stream()
            .filter(v -> OcmfUtils.endReading(v.ocmfRecord()) != null)
            .reduce((first, second) -> second)
            .orElse(null);

        // One record can carry both readings. Then it is enough to have it once.
        if (begin != null && end != null && begin.ocmfRecord().rawInput().equals(end.ocmfRecord().rawInput())) {
            end = null;
        }

        // publicKey of the meter in hex or base64, null if unknown
        var publicKey = signedValues.isEmpty()
            ? null
            : MeterPublicKeyExtractor.find(signedValues, meterPublicKeySources);

        var checks = SignedDataChecker.check(begin, end, publicKey, transaction);

        return new SignedTransaction(
            transaction.getId(),
            signedValues,
            begin,
            end,
            publicKey,
            checks
        );
    }

    private static SignedMeterValue parse(TransactionDetails.MeterValues meterValue) {
        var rawValue = meterValue.getValue();
        if (StringUtils.isBlank(rawValue)) {
            return error(meterValue, null, "Empty value");
        }

        var value = decodeBase64Maybe(rawValue.trim());
        if (value.startsWith(PREFIX)) {
            return fromOcmf(meterValue, value, null, null);
        }
        if (value.startsWith("{")) {
            return fromJsonWrapper(meterValue, value);
        }

        return error(meterValue, null, "Not an OCMF record");
    }

    private static SignedMeterValue fromJsonWrapper(TransactionDetails.MeterValues meterValue, String json) {
        SignedMeterValueType wrapper;
        try {
            wrapper = OcmfParser.MAPPER.readValue(json, SignedMeterValueType.class);
        } catch (JacksonException e) {
            return error(meterValue, null, "Malformed JSON: " + e.getOriginalMessage());
        }

        if (StringUtils.isEmpty(wrapper.signedMeterData())) {
            return error(meterValue, wrapper.publicKey(), "JSON has no 'signedMeterData'");
        }

        // OCPP 2.x says base64, but some stations put the plain OCMF string there
        var ocmf = decodeBase64Maybe(wrapper.signedMeterData().trim());

        if (!ocmf.startsWith(PREFIX)) {
            var error = "'signedMeterData' is not OCMF (encodingMethod=" + wrapper.encodingMethod() + ")";
            return error(meterValue, wrapper.publicKey(), error);
        }

        return fromOcmf(meterValue, ocmf, wrapper.publicKey(), wrapper.signingMethod());
    }

    private static SignedMeterValue fromOcmf(TransactionDetails.MeterValues meterValue, String ocmf,
                                             @Nullable String publicKey, @Nullable String signingMethod) {
        try {
            return new SignedMeterValue(meterValue, OcmfParser.parse(ocmf), publicKey, signingMethod, null);
        } catch (Exception e) {
            return error(meterValue, publicKey, "Malformed OCMF record: " + e.getMessage());
        }
    }

    private static SignedMeterValue error(TransactionDetails.MeterValues meterValue, @Nullable String publicKey,
                                          String error) {
        return new SignedMeterValue(meterValue, null, publicKey, null, error);
    }

    private static String decodeBase64Maybe(String raw) {
        if (isOcmfOrJson(raw)) {
            return raw;
        }

        var decoded = tryBase64(raw);
        if (decoded == null || !isOcmfOrJson(decoded)) {
            return raw;
        }

        return decoded;
    }

    private static boolean isOcmfOrJson(String value) {
        return value.startsWith(PREFIX) || value.startsWith("{");
    }

    @Nullable
    private static String tryBase64(String s) {
        try {
            return new String(Base64.getMimeDecoder().decode(s), StandardCharsets.UTF_8).trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}

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
import de.rwth.idsg.steve.repository.dto.TransactionMeterValue;
import de.rwth.idsg.steve.service.eichrecht.model.input.OcmfRecord;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.Encoding;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.Format;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.PublicKey;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.SignedReading;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.UnreadableValue;
import de.rwth.idsg.steve.service.eichrecht.model.SignedMeterValue;
import de.rwth.idsg.steve.service.eichrecht.model.input.SignedMeterValueType;
import de.rwth.idsg.steve.service.eichrecht.safe.ValuesXmlWriter;
import ocpp.cs._2015._10.ValueFormat;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;

import jakarta.xml.bind.JAXBException;
import java.util.List;
import java.util.function.Function;

import static de.rwth.idsg.steve.service.eichrecht.OcmfUtils.PREFIX;
import static de.rwth.idsg.steve.service.eichrecht.OcmfUtils.decodeBase64Maybe;

@Service
public class EichrechtService {

    private final ValuesXmlWriter valuesXmlWriter = new ValuesXmlWriter();

    /**
     * @param transactionId pairs the begin and end value in the S.A.F.E. XML
     */
    public String createXML(int transactionId, SignedMeterData signedMeterData) {
        try {
            return valuesXmlWriter.createXML(transactionId, signedMeterData);
        } catch (JAXBException e) {
            throw new SteveException("Could not create the S.A.F.E. XML of transaction %s", transactionId, e);
        }
    }

    public SignedMeterData getSignedMeterData(Transaction transaction,
                                              @Nullable MeterPublicKeySources meterPublicKeySources,
                                              List<TransactionMeterValue> values) {
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

        // publicKey of the meter in hex or base64, null if unknown
        var publicKey = signedValues.isEmpty()
            ? null
            : MeterPublicKeyExtractor.find(signedValues, meterPublicKeySources);

        var checks = SignedDataChecker.check(begin, end, publicKey, transaction);

        var unreadable = signedValues.stream()
            .filter(v -> v.parseError() != null)
            .map(v -> new UnreadableValue(v.meterValue().getValueTimestamp(), v.parseError()))
            .toList();

        return new SignedMeterData(
            signedValues.size(),
            toPublicKey(publicKey),
            toSignedReading(begin, OcmfUtils::beginReading),
            toSignedReading(end, OcmfUtils::endReading),
            checks,
            unreadable
        );
    }

    /**
     * Package-private for tests
     */
    static SignedMeterValue parse(TransactionMeterValue meterValue) {
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

    private static SignedMeterValue fromJsonWrapper(TransactionMeterValue meterValue, String json) {
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

    private static SignedMeterValue fromOcmf(TransactionMeterValue meterValue, String ocmf,
                                             @Nullable String publicKey, @Nullable String signingMethod) {
        try {
            return new SignedMeterValue(meterValue, OcmfParser.parse(ocmf), publicKey, signingMethod, null);
        } catch (Exception e) {
            return error(meterValue, publicKey, "Malformed OCMF record: " + e.getMessage());
        }
    }

    private static SignedMeterValue error(TransactionMeterValue meterValue, @Nullable String publicKey,
                                          String error) {
        return new SignedMeterValue(meterValue, null, publicKey, null, error);
    }

    @Nullable
    private static PublicKey toPublicKey(@Nullable String publicKey) {
        if (publicKey == null) {
            return null;
        }
        var encoding = MeterPublicKeyExtractor.isHex(publicKey) ? Encoding.hex : Encoding.base64;
        return new PublicKey(encoding, publicKey);
    }

    @Nullable
    private static SignedReading toSignedReading(@Nullable SignedMeterValue value,
                                                 Function<OcmfRecord, OcmfRecord.Reading> readingOf) {
        if (value == null) {
            return null;
        }
        var reading = readingOf.apply(value.ocmfRecord());
        return new SignedReading(
            value.meterValue().getValueTimestamp(),
            reading.value(),
            reading.unit(),
            SignedDataChecker.signingMethod(value),
            Format.OCMF,
            value.ocmfRecord().rawInput()
        );
    }
}

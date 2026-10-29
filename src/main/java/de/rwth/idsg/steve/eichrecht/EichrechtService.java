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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedDataParseResult;
import de.rwth.idsg.steve.eichrecht.SignedTransaction.SignedMeterValue;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import ocpp.cs._2015._10.ValueFormat;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static de.rwth.idsg.steve.eichrecht.OcmfParser.OCMF_PREFIX;

@Service
public class EichrechtService {

    private final OcmfParser ocmfParser = new OcmfParser();

    /**
     * Parses all meter values with format "SignedData" of one transaction. Other meter values are ignored.
     */
    public List<SignedMeterValue> parse(List<TransactionDetails.MeterValues> values) {
        String signedData = ValueFormat.SIGNED_DATA.value();

        return values.stream()
            .filter(it -> signedData.equals(it.getFormat()))
            .map(it -> new SignedMeterValue(it, this.parse(it.getValue())))
            .toList();
    }

    public SignedDataParseResult parse(@Nullable String rawValue) {
        if (Strings.isNullOrEmpty(rawValue)) {
            return unsupported("Empty value");
        }

        String trimmed = rawValue.trim();
        if (trimmed.startsWith(OCMF_PREFIX)) {
            return fromOcmf(trimmed, SignedDataSource.OCMF_PLAIN);
        }
        if (trimmed.startsWith("{")) {
            return fromJsonWrapper(trimmed);
        }

        String decoded = tryBase64(trimmed);
        if (decoded != null) {
            if (decoded.startsWith(OCMF_PREFIX)) {
                return fromOcmf(decoded, SignedDataSource.OCMF_BASE64);
            }
            if (decoded.startsWith("{")) {
                return fromJsonWrapper(decoded);
            }
        }

        return unsupported("Not an OCMF record");
    }

    private SignedDataParseResult fromOcmf(String val, SignedDataSource source) {
        var builder = SignedDataParseResult.builder().source(source);
        try {
            return builder.ocmfRecord(ocmfParser.parse(val)).build();
        } catch (Exception e) {
            return builder.parseError("Malformed OCMF record: " + e.getMessage()).build();
        }
    }

    private SignedDataParseResult fromJsonWrapper(String val) {
        SignedMeterValueType signedMeterWrapper;
        try {
            signedMeterWrapper = OcmfParser.MAPPER.readValue(val, SignedMeterValueType.class);
        } catch (JacksonException e) {
            return unsupported("Malformed JSON: " + e.getOriginalMessage());
        }

        var builder = SignedDataParseResult.builder()
            .source(SignedDataSource.JSON_WRAPPER)
            .signingMethod(signedMeterWrapper.signingMethod)
            .encodingMethod(signedMeterWrapper.encodingMethod)
            .publicKey(signedMeterWrapper.publicKey);

        var signedMeterData = signedMeterWrapper.signedMeterData;
        if (Strings.isNullOrEmpty(signedMeterData)) {
            return builder.parseError("JSON has no 'signedMeterData'").build();
        }

        // OCPP 2.x says base64, but some stations put the plain OCMF string there
        String ocmf = signedMeterData.startsWith(OCMF_PREFIX)
            ? signedMeterData
            : tryBase64(signedMeterData);

        if (ocmf == null || !ocmf.startsWith(OCMF_PREFIX)) {
            return builder.parseError("'signedMeterData' is not OCMF (encodingMethod=" + signedMeterWrapper.encodingMethod + ")").build();
        }

        try {
            return builder.ocmfRecord(ocmfParser.parse(ocmf)).build();
        } catch (Exception e) {
            return builder.parseError("Malformed OCMF record: " + e.getMessage()).build();
        }
    }

    @Nullable
    private static String tryBase64(String s) {
        try {
            String out = new String(Base64.getMimeDecoder().decode(s), StandardCharsets.UTF_8);
            return out.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static SignedDataParseResult unsupported(String error) {
        return SignedDataParseResult.builder()
            .source(SignedDataSource.UNSUPPORTED)
            .parseError(error)
            .build();
    }

    public record SignedMeterValueType(
        String signedMeterData,
        String signingMethod,
        String encodingMethod,
        String publicKey
    ) {
    }
}

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
package de.rwth.idsg.steve.service.eichrecht.safe;

import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.PublicKey;
import de.rwth.idsg.steve.service.eichrecht.model.output.SignedMeterData.SignedReading;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Writes the signed begin and end records of a transaction in the S.A.F.E. XML format.
 * The transparency software pairs the two by their transactionId and shows the energy between them.
 * <p>
 * If there is only one record (it carries both readings, or begin or end is missing), we leave out the
 * transactionId. Otherwise, the transparency software tries to verify it as a transaction, which needs exactly
 * one begin and one end value, and fails. Without it, the record is verified on its own. If it carries both
 * readings, the software still recognizes it as a complete transaction.
 */
public class ValuesXmlWriter {

    private final JAXBContext jc;

    public ValuesXmlWriter() {
        try {
            jc = JAXBContext.newInstance(Values.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param transactionId pairs the begin and end value
     */
    public String createXML(int transactionId, SignedMeterData signedMeterData) throws JAXBException {
        var marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        var sw = new StringWriter();
        marshaller.marshal(toValues(transactionId, signedMeterData), sw);
        return sw.toString();
    }

    private static Values toValues(int transactionId, SignedMeterData data) {
        var begin = data.begin();

        // One record with both readings goes into the file once
        var end = (begin != null && data.end() != null && begin.signedData().equals(data.end().signedData()))
            ? null
            : data.end();

        Integer pairId = (begin != null && end != null)
            ? transactionId
            : null;

        var values = new ArrayList<Values.Value>();
        if (begin != null) {
            values.add(toValue("Transaction.Begin", begin, pairId, data.publicKey()));
        }
        if (end != null) {
            values.add(toValue("Transaction.End", end, pairId, data.publicKey()));
        }
        return new Values().setValues(values);
    }

    private static Values.Value toValue(String context,
                                        SignedReading reading,
                                        @Nullable Integer transactionId,
                                        @Nullable PublicKey publicKey) {
        return new Values.Value()
            .setContext(context)
            .setTransactionId(transactionId)
            .setPublicKey(toPublicKey(publicKey))
            .setSignedData(new Values.SignedData()
                .setFormat(reading.format().name())
                .setEncoding(Values.EncodingType.plain)
                .setValue(reading.signedData())
            );
    }

    /**
     * The public key is optional in S.A.F.E. XML. If we do not know it, we leave it out, because the transparency
     * software rejects an empty publicKey element. The user can then enter the key in the software.
     */
    @Nullable
    private static Values.PublicKey toPublicKey(@Nullable PublicKey publicKey) {
        if (publicKey == null) {
            return null;
        }
        var encoding = switch (publicKey.encoding()) {
            case hex -> Values.EncodingType.hex;
            case base64 -> Values.EncodingType.base64;
        };
        return new Values.PublicKey()
            .setEncoding(encoding)
            .setValue(publicKey.value());
    }
}

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

import de.rwth.idsg.steve.service.eichrecht.MeterPublicKeyService;
import de.rwth.idsg.steve.service.eichrecht.OcmfRecord;
import de.rwth.idsg.steve.service.eichrecht.SignedTransaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes the signed begin and end records of a transaction in the S.A.F.E. XML format.
 * The transparency software pairs the two by their transactionId and shows the energy between them.
 * <p>
 * If there is only one record (it carries both readings, or begin or end is missing), we leave out the
 * transactionId. Otherwise, the transparency software tries to verify it as a transaction, which needs exactly
 * one begin and one end value, and fails. Without it, the record is verified on its own. If it carries both
 * readings, the software still recognizes it as a complete transaction.
 */
@Service
public class ValuesXmlWriter {

    private final JAXBContext jc;

    public ValuesXmlWriter() throws JAXBException {
        jc = JAXBContext.newInstance(Values.class);
    }

    public String createXML(SignedTransaction signed) throws JAXBException {
        var marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(toValues(signed), sw);
        return sw.toString();
    }

    private static Values toValues(SignedTransaction signed) {
        Integer transactionId = (signed.begin() != null && signed.end() != null) ? signed.transactionId() : null;

        List<Values.Value> values = new ArrayList<>();
        if (signed.begin() != null) {
            values.add(toValue("Transaction.Begin", signed.begin(), transactionId, signed.publicKey()));
        }
        if (signed.end() != null) {
            values.add(toValue("Transaction.End", signed.end(), transactionId, signed.publicKey()));
        }
        return new Values().setValues(values);
    }

    private static Values.Value toValue(String context,
                                        OcmfRecord record,
                                        @Nullable Integer transactionId,
                                        @Nullable String publicKey) {
        return new Values.Value()
            .setContext(context)
            .setTransactionId(transactionId)
            .setPublicKey(toPublicKey(publicKey))
            .setSignedData(new Values.SignedData()
                .setFormat("OCMF")
                .setEncoding(Values.EncodingType.plain)
                .setValue(record.rawInput())
            );
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
            .setEncoding(MeterPublicKeyService.isHex(publicKey) ? Values.EncodingType.hex : Values.EncodingType.base64)
            .setValue(publicKey);
    }
}

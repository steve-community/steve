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

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.List;

/**
 * https://github.com/SAFE-eV/transparenzsoftware
 * https://github.com/SAFE-eV/transparenzsoftware/blob/main/XML_Format.md
 * See also the package de.safe_ev.transparenzsoftware.verification.xml in the repository above.
 */
@Accessors(chain = true)
@Data
@XmlRootElement(name = "values")
@XmlAccessorType(XmlAccessType.FIELD)
public class Values {

    @XmlElement(name = "value") private List<Value> values;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Value {
        @XmlAttribute private String context;
        @XmlAttribute private Integer transactionId;
        private PublicKey publicKey;
        private SignedData signedData;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PublicKey {
        @XmlAttribute private EncodingType encoding;
        @XmlValue private String value;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SignedData {
        @XmlAttribute private String format;
        @XmlAttribute private EncodingType encoding;
        @XmlValue private String value;
    }

    public enum EncodingType {
        base64,
        base32,
        plain,
        hex
    }
}


package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataTransferStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTransferStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Accepted"/&gt;
 *     &lt;enumeration value="Rejected"/&gt;
 *     &lt;enumeration value="UnknownMessageId"/&gt;
 *     &lt;enumeration value="UnknownVendorId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DataTransferStatus")
@XmlEnum
public enum DataTransferStatus {

    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),
    @XmlEnumValue("Rejected")
    REJECTED("Rejected"),
    @XmlEnumValue("UnknownMessageId")
    UNKNOWN_MESSAGE_ID("UnknownMessageId"),
    @XmlEnumValue("UnknownVendorId")
    UNKNOWN_VENDOR_ID("UnknownVendorId");
    private final String value;

    DataTransferStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTransferStatus fromValue(String v) {
        for (DataTransferStatus c: DataTransferStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

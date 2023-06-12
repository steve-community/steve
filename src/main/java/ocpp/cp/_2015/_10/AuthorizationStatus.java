
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuthorizationStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AuthorizationStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Accepted"/&gt;
 *     &lt;enumeration value="Blocked"/&gt;
 *     &lt;enumeration value="Expired"/&gt;
 *     &lt;enumeration value="Invalid"/&gt;
 *     &lt;enumeration value="ConcurrentTx"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AuthorizationStatus")
@XmlEnum
public enum AuthorizationStatus {

    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),
    @XmlEnumValue("Blocked")
    BLOCKED("Blocked"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired"),
    @XmlEnumValue("Invalid")
    INVALID("Invalid"),
    @XmlEnumValue("ConcurrentTx")
    CONCURRENT_TX("ConcurrentTx");
    private final String value;

    AuthorizationStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AuthorizationStatus fromValue(String v) {
        for (AuthorizationStatus c: AuthorizationStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

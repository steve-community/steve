
package ocpp.cs._2012._06;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Location.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Location"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Inlet"/&gt;
 *     &lt;enumeration value="Outlet"/&gt;
 *     &lt;enumeration value="Body"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Location")
@XmlEnum
public enum Location {

    @XmlEnumValue("Inlet")
    INLET("Inlet"),
    @XmlEnumValue("Outlet")
    OUTLET("Outlet"),
    @XmlEnumValue("Body")
    BODY("Body");
    private final String value;

    Location(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Location fromValue(String v) {
        for (Location c: Location.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

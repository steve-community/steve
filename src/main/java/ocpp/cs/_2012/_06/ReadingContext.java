
package ocpp.cs._2012._06;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadingContext.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ReadingContext"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Interruption.Begin"/&gt;
 *     &lt;enumeration value="Interruption.End"/&gt;
 *     &lt;enumeration value="Sample.Clock"/&gt;
 *     &lt;enumeration value="Sample.Periodic"/&gt;
 *     &lt;enumeration value="Transaction.Begin"/&gt;
 *     &lt;enumeration value="Transaction.End"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ReadingContext")
@XmlEnum
public enum ReadingContext {

    @XmlEnumValue("Interruption.Begin")
    INTERRUPTION_BEGIN("Interruption.Begin"),
    @XmlEnumValue("Interruption.End")
    INTERRUPTION_END("Interruption.End"),
    @XmlEnumValue("Sample.Clock")
    SAMPLE_CLOCK("Sample.Clock"),
    @XmlEnumValue("Sample.Periodic")
    SAMPLE_PERIODIC("Sample.Periodic"),
    @XmlEnumValue("Transaction.Begin")
    TRANSACTION_BEGIN("Transaction.Begin"),
    @XmlEnumValue("Transaction.End")
    TRANSACTION_END("Transaction.End");
    private final String value;

    ReadingContext(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReadingContext fromValue(String v) {
        for (ReadingContext c: ReadingContext.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

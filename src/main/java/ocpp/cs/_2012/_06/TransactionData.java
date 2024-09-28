
package ocpp.cs._2012._06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * This contains transaction usage details relevant for billing purposes in StopTransaction.req PDU
 * 
 * <p>Java class for TransactionData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="values" type="{urn://Ocpp/Cs/2012/06/}MeterValue" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionData", propOrder = {
    "values"
})
@ToString
public class TransactionData {

    protected List<MeterValue> values;

    /**
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MeterValue }
     * 
     * 
     */
    public List<MeterValue> getValues() {
        if (values == null) {
            values = new ArrayList<MeterValue>();
        }
        return this.values;
    }

    public boolean isSetValues() {
        return ((this.values!= null)&&(!this.values.isEmpty()));
    }

    public void unsetValues() {
        this.values = null;
    }

    /**
     * Adds objects to the list of Values using add method
     * 
     * @param values
     *     objects to add to the list Values
     * @return
     *     The class instance
     */
    public TransactionData withValues(MeterValue... values) {
        if (values!= null) {
            for (MeterValue value: values) {
                getValues().add(value);
            }
        }
        return this;
    }

    /**
     * Adds objects to the list of Values using addAll method
     * 
     * @param values
     *     objects to add to the list Values
     * @return
     *     The class instance
     */
    public TransactionData withValues(Collection<MeterValue> values) {
        if (values!= null) {
            getValues().addAll(values);
        }
        return this;
    }

}

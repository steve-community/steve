
package ocpp.cs._2015._10;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the MeterValues.req PDU
 * 
 * <p>Java class for MeterValuesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MeterValuesRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="meterValue" type="{urn://Ocpp/Cs/2015/10/}MeterValue" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterValuesRequest", propOrder = {
    "connectorId",
    "transactionId",
    "meterValue"
})
@ToString
public class MeterValuesRequest
    implements RequestType
{

    protected int connectorId;
    protected Integer transactionId;
    protected List<MeterValue> meterValue;

    /**
     * Gets the value of the connectorId property.
     * 
     */
    public int getConnectorId() {
        return connectorId;
    }

    /**
     * Sets the value of the connectorId property.
     * 
     */
    public void setConnectorId(int value) {
        this.connectorId = value;
    }

    public boolean isSetConnectorId() {
        return true;
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTransactionId(Integer value) {
        this.transactionId = value;
    }

    public boolean isSetTransactionId() {
        return (this.transactionId!= null);
    }

    /**
     * Gets the value of the meterValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the meterValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMeterValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MeterValue }
     * 
     * 
     */
    public List<MeterValue> getMeterValue() {
        if (meterValue == null) {
            meterValue = new ArrayList<MeterValue>();
        }
        return this.meterValue;
    }

    public boolean isSetMeterValue() {
        return ((this.meterValue!= null)&&(!this.meterValue.isEmpty()));
    }

    public void unsetMeterValue() {
        this.meterValue = null;
    }

    public MeterValuesRequest withConnectorId(int value) {
        setConnectorId(value);
        return this;
    }

    public MeterValuesRequest withTransactionId(Integer value) {
        setTransactionId(value);
        return this;
    }

    public MeterValuesRequest withMeterValue(MeterValue... values) {
        if (values!= null) {
            for (MeterValue value: values) {
                getMeterValue().add(value);
            }
        }
        return this;
    }

    public MeterValuesRequest withMeterValue(Collection<MeterValue> values) {
        if (values!= null) {
            getMeterValue().addAll(values);
        }
        return this;
    }

}

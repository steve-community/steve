
package ocpp.cs._2012._06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * Defines the StopTransaction.req PDU
 * 
 * <p>Java class for StopTransactionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StopTransactionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="idTag" type="{urn://Ocpp/Cs/2012/06/}IdToken" minOccurs="0"/&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="meterStop" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="transactionData" type="{urn://Ocpp/Cs/2012/06/}TransactionData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopTransactionRequest", propOrder = {
    "transactionId",
    "idTag",
    "timestamp",
    "meterStop",
    "transactionData"
})
@ToString
public class StopTransactionRequest
    implements RequestType
{

    protected int transactionId;
    protected String idTag;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime timestamp;
    protected int meterStop;
    protected List<TransactionData> transactionData;

    /**
     * Gets the value of the transactionId property.
     * 
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     */
    public void setTransactionId(int value) {
        this.transactionId = value;
    }

    public boolean isSetTransactionId() {
        return true;
    }

    /**
     * Gets the value of the idTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTag() {
        return idTag;
    }

    /**
     * Sets the value of the idTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTag(String value) {
        this.idTag = value;
    }

    public boolean isSetIdTag() {
        return (this.idTag!= null);
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(DateTime value) {
        this.timestamp = value;
    }

    public boolean isSetTimestamp() {
        return (this.timestamp!= null);
    }

    /**
     * Gets the value of the meterStop property.
     * 
     */
    public int getMeterStop() {
        return meterStop;
    }

    /**
     * Sets the value of the meterStop property.
     * 
     */
    public void setMeterStop(int value) {
        this.meterStop = value;
    }

    public boolean isSetMeterStop() {
        return true;
    }

    /**
     * Gets the value of the transactionData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionData }
     * 
     * 
     */
    public List<TransactionData> getTransactionData() {
        if (transactionData == null) {
            transactionData = new ArrayList<TransactionData>();
        }
        return this.transactionData;
    }

    public boolean isSetTransactionData() {
        return ((this.transactionData!= null)&&(!this.transactionData.isEmpty()));
    }

    public void unsetTransactionData() {
        this.transactionData = null;
    }

    public StopTransactionRequest withTransactionId(int value) {
        setTransactionId(value);
        return this;
    }

    public StopTransactionRequest withIdTag(String value) {
        setIdTag(value);
        return this;
    }

    public StopTransactionRequest withTimestamp(DateTime value) {
        setTimestamp(value);
        return this;
    }

    public StopTransactionRequest withMeterStop(int value) {
        setMeterStop(value);
        return this;
    }

    public StopTransactionRequest withTransactionData(TransactionData... values) {
        if (values!= null) {
            for (TransactionData value: values) {
                getTransactionData().add(value);
            }
        }
        return this;
    }

    public StopTransactionRequest withTransactionData(Collection<TransactionData> values) {
        if (values!= null) {
            getTransactionData().addAll(values);
        }
        return this;
    }

}

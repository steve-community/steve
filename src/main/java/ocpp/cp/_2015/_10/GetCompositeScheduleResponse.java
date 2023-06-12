
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.rwth.idsg.ocpp.jaxb.JodaDateTimeConverter;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * Defines the GetCompositeSchedule.conf PDU
 * 
 * <p>Java class for GetCompositeScheduleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCompositeScheduleResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{urn://Ocpp/Cp/2015/10/}GetCompositeScheduleStatus"/&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="scheduleStart" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="chargingSchedule" type="{urn://Ocpp/Cp/2015/10/}ChargingSchedule" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCompositeScheduleResponse", propOrder = {
    "status",
    "connectorId",
    "scheduleStart",
    "chargingSchedule"
})
@ToString
public class GetCompositeScheduleResponse
    implements ResponseType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected GetCompositeScheduleStatus status;
    protected Integer connectorId;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime scheduleStart;
    protected ChargingSchedule chargingSchedule;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link GetCompositeScheduleStatus }
     *     
     */
    public GetCompositeScheduleStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetCompositeScheduleStatus }
     *     
     */
    public void setStatus(GetCompositeScheduleStatus value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    /**
     * Gets the value of the connectorId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Sets the value of the connectorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setConnectorId(Integer value) {
        this.connectorId = value;
    }

    public boolean isSetConnectorId() {
        return (this.connectorId!= null);
    }

    /**
     * Gets the value of the scheduleStart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getScheduleStart() {
        return scheduleStart;
    }

    /**
     * Sets the value of the scheduleStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduleStart(DateTime value) {
        this.scheduleStart = value;
    }

    public boolean isSetScheduleStart() {
        return (this.scheduleStart!= null);
    }

    /**
     * Gets the value of the chargingSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingSchedule }
     *     
     */
    public ChargingSchedule getChargingSchedule() {
        return chargingSchedule;
    }

    /**
     * Sets the value of the chargingSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingSchedule }
     *     
     */
    public void setChargingSchedule(ChargingSchedule value) {
        this.chargingSchedule = value;
    }

    public boolean isSetChargingSchedule() {
        return (this.chargingSchedule!= null);
    }

    public GetCompositeScheduleResponse withStatus(GetCompositeScheduleStatus value) {
        setStatus(value);
        return this;
    }

    public GetCompositeScheduleResponse withConnectorId(Integer value) {
        setConnectorId(value);
        return this;
    }

    public GetCompositeScheduleResponse withScheduleStart(DateTime value) {
        setScheduleStart(value);
        return this;
    }

    public GetCompositeScheduleResponse withChargingSchedule(ChargingSchedule value) {
        setChargingSchedule(value);
        return this;
    }

}

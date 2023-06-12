
package ocpp.cp._2015._10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.ToString;


/**
 * Defines the GetCompositeSchedule.req PDU
 * 
 * <p>Java class for GetCompositeScheduleRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCompositeScheduleRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="connectorId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="chargingRateUnit" type="{urn://Ocpp/Cp/2015/10/}ChargingRateUnitType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCompositeScheduleRequest", propOrder = {
    "connectorId",
    "duration",
    "chargingRateUnit"
})
@ToString
public class GetCompositeScheduleRequest
    implements RequestType
{

    protected int connectorId;
    protected int duration;
    @XmlSchemaType(name = "string")
    protected ChargingRateUnitType chargingRateUnit;

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
     * Gets the value of the duration property.
     * 
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     */
    public void setDuration(int value) {
        this.duration = value;
    }

    public boolean isSetDuration() {
        return true;
    }

    /**
     * Gets the value of the chargingRateUnit property.
     * 
     * @return
     *     possible object is
     *     {@link ChargingRateUnitType }
     *     
     */
    public ChargingRateUnitType getChargingRateUnit() {
        return chargingRateUnit;
    }

    /**
     * Sets the value of the chargingRateUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingRateUnitType }
     *     
     */
    public void setChargingRateUnit(ChargingRateUnitType value) {
        this.chargingRateUnit = value;
    }

    public boolean isSetChargingRateUnit() {
        return (this.chargingRateUnit!= null);
    }

    public GetCompositeScheduleRequest withConnectorId(int value) {
        setConnectorId(value);
        return this;
    }

    public GetCompositeScheduleRequest withDuration(int value) {
        setDuration(value);
        return this;
    }

    public GetCompositeScheduleRequest withChargingRateUnit(ChargingRateUnitType value) {
        setChargingRateUnit(value);
        return this;
    }

}

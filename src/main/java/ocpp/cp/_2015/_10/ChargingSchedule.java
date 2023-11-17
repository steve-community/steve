
package ocpp.cp._2015._10;

import java.math.BigDecimal;
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
import lombok.ToString;
import org.joda.time.DateTime;


/**
 * <p>Java class for ChargingSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargingSchedule"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="startSchedule" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="chargingRateUnit" type="{urn://Ocpp/Cp/2015/10/}ChargingRateUnitType"/&gt;
 *         &lt;element name="chargingSchedulePeriod" type="{urn://Ocpp/Cp/2015/10/}ChargingSchedulePeriod" maxOccurs="unbounded"/&gt;
 *         &lt;element name="minChargingRate" type="{urn://Ocpp/Cp/2015/10/}DecimalOne" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargingSchedule", propOrder = {
    "duration",
    "startSchedule",
    "chargingRateUnit",
    "chargingSchedulePeriod",
    "minChargingRate"
})
@ToString
public class ChargingSchedule {

    protected Integer duration;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(JodaDateTimeConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime startSchedule;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChargingRateUnitType chargingRateUnit;
    @XmlElement(required = true)
    protected List<ChargingSchedulePeriod> chargingSchedulePeriod;
    protected BigDecimal minChargingRate;

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDuration(Integer value) {
        this.duration = value;
    }

    public boolean isSetDuration() {
        return (this.duration!= null);
    }

    /**
     * Gets the value of the startSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getStartSchedule() {
        return startSchedule;
    }

    /**
     * Sets the value of the startSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartSchedule(DateTime value) {
        this.startSchedule = value;
    }

    public boolean isSetStartSchedule() {
        return (this.startSchedule!= null);
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

    /**
     * Gets the value of the chargingSchedulePeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chargingSchedulePeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChargingSchedulePeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChargingSchedulePeriod }
     * 
     * 
     */
    public List<ChargingSchedulePeriod> getChargingSchedulePeriod() {
        if (chargingSchedulePeriod == null) {
            chargingSchedulePeriod = new ArrayList<ChargingSchedulePeriod>();
        }
        return this.chargingSchedulePeriod;
    }

    public boolean isSetChargingSchedulePeriod() {
        return ((this.chargingSchedulePeriod!= null)&&(!this.chargingSchedulePeriod.isEmpty()));
    }

    public void unsetChargingSchedulePeriod() {
        this.chargingSchedulePeriod = null;
    }

    /**
     * Gets the value of the minChargingRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMinChargingRate() {
        return minChargingRate;
    }

    /**
     * Sets the value of the minChargingRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMinChargingRate(BigDecimal value) {
        this.minChargingRate = value;
    }

    public boolean isSetMinChargingRate() {
        return (this.minChargingRate!= null);
    }

    public ChargingSchedule withDuration(Integer value) {
        setDuration(value);
        return this;
    }

    public ChargingSchedule withStartSchedule(DateTime value) {
        setStartSchedule(value);
        return this;
    }

    public ChargingSchedule withChargingRateUnit(ChargingRateUnitType value) {
        setChargingRateUnit(value);
        return this;
    }

    public ChargingSchedule withChargingSchedulePeriod(ChargingSchedulePeriod... values) {
        if (values!= null) {
            for (ChargingSchedulePeriod value: values) {
                getChargingSchedulePeriod().add(value);
            }
        }
        return this;
    }

    public ChargingSchedule withChargingSchedulePeriod(Collection<ChargingSchedulePeriod> values) {
        if (values!= null) {
            getChargingSchedulePeriod().addAll(values);
        }
        return this;
    }

    public ChargingSchedule withMinChargingRate(BigDecimal value) {
        setMinChargingRate(value);
        return this;
    }

}

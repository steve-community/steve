
package ocpp.cp._2015._10;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.ToString;


/**
 * <p>Java class for ChargingSchedulePeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargingSchedulePeriod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="startPeriod" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="limit" type="{urn://Ocpp/Cp/2015/10/}DecimalOne"/&gt;
 *         &lt;element name="numberPhases" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargingSchedulePeriod", propOrder = {
    "startPeriod",
    "limit",
    "numberPhases"
})
@ToString
public class ChargingSchedulePeriod {

    protected int startPeriod;
    @XmlElement(required = true)
    protected BigDecimal limit;
    protected Integer numberPhases;

    /**
     * Gets the value of the startPeriod property.
     * 
     */
    public int getStartPeriod() {
        return startPeriod;
    }

    /**
     * Sets the value of the startPeriod property.
     * 
     */
    public void setStartPeriod(int value) {
        this.startPeriod = value;
    }

    public boolean isSetStartPeriod() {
        return true;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLimit(BigDecimal value) {
        this.limit = value;
    }

    public boolean isSetLimit() {
        return (this.limit!= null);
    }

    /**
     * Gets the value of the numberPhases property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberPhases() {
        return numberPhases;
    }

    /**
     * Sets the value of the numberPhases property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberPhases(Integer value) {
        this.numberPhases = value;
    }

    public boolean isSetNumberPhases() {
        return (this.numberPhases!= null);
    }

    /**
     * Sets the value of the startPeriod property.
     * 
     * @param value
     *     allowed object is
     *     int
     * @return
     *     The class instance
     */
    public ChargingSchedulePeriod withStartPeriod(int value) {
        setStartPeriod(value);
        return this;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     * @return
     *     The class instance
     */
    public ChargingSchedulePeriod withLimit(BigDecimal value) {
        setLimit(value);
        return this;
    }

    /**
     * Sets the value of the numberPhases property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     * @return
     *     The class instance
     */
    public ChargingSchedulePeriod withNumberPhases(Integer value) {
        setNumberPhases(value);
        return this;
    }

}

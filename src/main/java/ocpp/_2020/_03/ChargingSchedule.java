
package ocpp._2020._03;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.joda.time.DateTime;


/**
 * Charging_ Schedule
 * urn:x-oca:ocpp:uid:2:233256
 * Charging schedule structure defines a list of charging periods, as used in: GetCompositeSchedule.conf and ChargingProfile. 
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "id",
    "startSchedule",
    "duration",
    "chargingRateUnit",
    "chargingSchedulePeriod",
    "minChargingRate",
    "salesTariff"
})
@Generated("jsonschema2pojo")
public class ChargingSchedule {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Identifies the ChargingSchedule.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Identifies the ChargingSchedule.\r\n")
    @NotNull
    private Integer id;
    /**
     * Charging_ Schedule. Start_ Schedule. Date_ Time
     * urn:x-oca:ocpp:uid:1:569237
     * Starting point of an absolute schedule. If absent the schedule will be relative to start of charging.
     * 
     * 
     */
    @JsonProperty("startSchedule")
    @JsonPropertyDescription("Charging_ Schedule. Start_ Schedule. Date_ Time\r\nurn:x-oca:ocpp:uid:1:569237\r\nStarting point of an absolute schedule. If absent the schedule will be relative to start of charging.\r\n")
    private DateTime startSchedule;
    /**
     * Charging_ Schedule. Duration. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569236
     * Duration of the charging schedule in seconds. If the duration is left empty, the last period will continue indefinitely or until end of the transaction if chargingProfilePurpose = TxProfile.
     * 
     * 
     */
    @JsonProperty("duration")
    @JsonPropertyDescription("Charging_ Schedule. Duration. Elapsed_ Time\r\nurn:x-oca:ocpp:uid:1:569236\r\nDuration of the charging schedule in seconds. If the duration is left empty, the last period will continue indefinitely or until end of the transaction if chargingProfilePurpose = TxProfile.\r\n")
    private Integer duration;
    /**
     * Charging_ Schedule. Charging_ Rate_ Unit. Charging_ Rate_ Unit_ Code
     * urn:x-oca:ocpp:uid:1:569238
     * The unit of measure Limit is expressed in.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingRateUnit")
    @JsonPropertyDescription("Charging_ Schedule. Charging_ Rate_ Unit. Charging_ Rate_ Unit_ Code\r\nurn:x-oca:ocpp:uid:1:569238\r\nThe unit of measure Limit is expressed in.\r\n")
    @NotNull
    private ChargingRateUnitEnum chargingRateUnit;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingSchedulePeriod")
    @Size(min = 1, max = 1024)
    @Valid
    @NotNull
    private List<ChargingSchedulePeriod> chargingSchedulePeriod = new ArrayList<ChargingSchedulePeriod>();
    /**
     * Charging_ Schedule. Min_ Charging_ Rate. Numeric
     * urn:x-oca:ocpp:uid:1:569239
     * Minimum charging rate supported by the EV. The unit of measure is defined by the chargingRateUnit. This parameter is intended to be used by a local smart charging algorithm to optimize the power allocation for in the case a charging process is inefficient at lower charging rates. Accepts at most one digit fraction (e.g. 8.1)
     * 
     * 
     */
    @JsonProperty("minChargingRate")
    @JsonPropertyDescription("Charging_ Schedule. Min_ Charging_ Rate. Numeric\r\nurn:x-oca:ocpp:uid:1:569239\r\nMinimum charging rate supported by the EV. The unit of measure is defined by the chargingRateUnit. This parameter is intended to be used by a local smart charging algorithm to optimize the power allocation for in the case a charging process is inefficient at lower charging rates. Accepts at most one digit fraction (e.g. 8.1)\r\n")
    private Double minChargingRate;
    /**
     * Sales_ Tariff
     * urn:x-oca:ocpp:uid:2:233272
     * NOTE: This dataType is based on dataTypes from &lt;&lt;ref-ISOIEC15118-2,ISO 15118-2&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("salesTariff")
    @JsonPropertyDescription("Sales_ Tariff\r\nurn:x-oca:ocpp:uid:2:233272\r\nNOTE: This dataType is based on dataTypes from &lt;&lt;ref-ISOIEC15118-2,ISO 15118-2&gt;&gt;.\r\n")
    @Valid
    private SalesTariff salesTariff;

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    public CustomData getCustomData() {
        return customData;
    }

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

    public ChargingSchedule withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Identifies the ChargingSchedule.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * Identifies the ChargingSchedule.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public ChargingSchedule withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Charging_ Schedule. Start_ Schedule. Date_ Time
     * urn:x-oca:ocpp:uid:1:569237
     * Starting point of an absolute schedule. If absent the schedule will be relative to start of charging.
     * 
     * 
     */
    @JsonProperty("startSchedule")
    public DateTime getStartSchedule() {
        return startSchedule;
    }

    /**
     * Charging_ Schedule. Start_ Schedule. Date_ Time
     * urn:x-oca:ocpp:uid:1:569237
     * Starting point of an absolute schedule. If absent the schedule will be relative to start of charging.
     * 
     * 
     */
    @JsonProperty("startSchedule")
    public void setStartSchedule(DateTime startSchedule) {
        this.startSchedule = startSchedule;
    }

    public ChargingSchedule withStartSchedule(DateTime startSchedule) {
        this.startSchedule = startSchedule;
        return this;
    }

    /**
     * Charging_ Schedule. Duration. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569236
     * Duration of the charging schedule in seconds. If the duration is left empty, the last period will continue indefinitely or until end of the transaction if chargingProfilePurpose = TxProfile.
     * 
     * 
     */
    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    /**
     * Charging_ Schedule. Duration. Elapsed_ Time
     * urn:x-oca:ocpp:uid:1:569236
     * Duration of the charging schedule in seconds. If the duration is left empty, the last period will continue indefinitely or until end of the transaction if chargingProfilePurpose = TxProfile.
     * 
     * 
     */
    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ChargingSchedule withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Charging_ Schedule. Charging_ Rate_ Unit. Charging_ Rate_ Unit_ Code
     * urn:x-oca:ocpp:uid:1:569238
     * The unit of measure Limit is expressed in.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingRateUnit")
    public ChargingRateUnitEnum getChargingRateUnit() {
        return chargingRateUnit;
    }

    /**
     * Charging_ Schedule. Charging_ Rate_ Unit. Charging_ Rate_ Unit_ Code
     * urn:x-oca:ocpp:uid:1:569238
     * The unit of measure Limit is expressed in.
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingRateUnit")
    public void setChargingRateUnit(ChargingRateUnitEnum chargingRateUnit) {
        this.chargingRateUnit = chargingRateUnit;
    }

    public ChargingSchedule withChargingRateUnit(ChargingRateUnitEnum chargingRateUnit) {
        this.chargingRateUnit = chargingRateUnit;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingSchedulePeriod")
    public List<ChargingSchedulePeriod> getChargingSchedulePeriod() {
        return chargingSchedulePeriod;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("chargingSchedulePeriod")
    public void setChargingSchedulePeriod(List<ChargingSchedulePeriod> chargingSchedulePeriod) {
        this.chargingSchedulePeriod = chargingSchedulePeriod;
    }

    public ChargingSchedule withChargingSchedulePeriod(List<ChargingSchedulePeriod> chargingSchedulePeriod) {
        this.chargingSchedulePeriod = chargingSchedulePeriod;
        return this;
    }

    /**
     * Charging_ Schedule. Min_ Charging_ Rate. Numeric
     * urn:x-oca:ocpp:uid:1:569239
     * Minimum charging rate supported by the EV. The unit of measure is defined by the chargingRateUnit. This parameter is intended to be used by a local smart charging algorithm to optimize the power allocation for in the case a charging process is inefficient at lower charging rates. Accepts at most one digit fraction (e.g. 8.1)
     * 
     * 
     */
    @JsonProperty("minChargingRate")
    public Double getMinChargingRate() {
        return minChargingRate;
    }

    /**
     * Charging_ Schedule. Min_ Charging_ Rate. Numeric
     * urn:x-oca:ocpp:uid:1:569239
     * Minimum charging rate supported by the EV. The unit of measure is defined by the chargingRateUnit. This parameter is intended to be used by a local smart charging algorithm to optimize the power allocation for in the case a charging process is inefficient at lower charging rates. Accepts at most one digit fraction (e.g. 8.1)
     * 
     * 
     */
    @JsonProperty("minChargingRate")
    public void setMinChargingRate(Double minChargingRate) {
        this.minChargingRate = minChargingRate;
    }

    public ChargingSchedule withMinChargingRate(Double minChargingRate) {
        this.minChargingRate = minChargingRate;
        return this;
    }

    /**
     * Sales_ Tariff
     * urn:x-oca:ocpp:uid:2:233272
     * NOTE: This dataType is based on dataTypes from &lt;&lt;ref-ISOIEC15118-2,ISO 15118-2&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("salesTariff")
    public SalesTariff getSalesTariff() {
        return salesTariff;
    }

    /**
     * Sales_ Tariff
     * urn:x-oca:ocpp:uid:2:233272
     * NOTE: This dataType is based on dataTypes from &lt;&lt;ref-ISOIEC15118-2,ISO 15118-2&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("salesTariff")
    public void setSalesTariff(SalesTariff salesTariff) {
        this.salesTariff = salesTariff;
    }

    public ChargingSchedule withSalesTariff(SalesTariff salesTariff) {
        this.salesTariff = salesTariff;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChargingSchedule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("startSchedule");
        sb.append('=');
        sb.append(((this.startSchedule == null)?"<null>":this.startSchedule));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("chargingRateUnit");
        sb.append('=');
        sb.append(((this.chargingRateUnit == null)?"<null>":this.chargingRateUnit));
        sb.append(',');
        sb.append("chargingSchedulePeriod");
        sb.append('=');
        sb.append(((this.chargingSchedulePeriod == null)?"<null>":this.chargingSchedulePeriod));
        sb.append(',');
        sb.append("minChargingRate");
        sb.append('=');
        sb.append(((this.minChargingRate == null)?"<null>":this.minChargingRate));
        sb.append(',');
        sb.append("salesTariff");
        sb.append('=');
        sb.append(((this.salesTariff == null)?"<null>":this.salesTariff));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.duration == null)? 0 :this.duration.hashCode()));
        result = ((result* 31)+((this.minChargingRate == null)? 0 :this.minChargingRate.hashCode()));
        result = ((result* 31)+((this.startSchedule == null)? 0 :this.startSchedule.hashCode()));
        result = ((result* 31)+((this.chargingSchedulePeriod == null)? 0 :this.chargingSchedulePeriod.hashCode()));
        result = ((result* 31)+((this.salesTariff == null)? 0 :this.salesTariff.hashCode()));
        result = ((result* 31)+((this.chargingRateUnit == null)? 0 :this.chargingRateUnit.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ChargingSchedule) == false) {
            return false;
        }
        ChargingSchedule rhs = ((ChargingSchedule) other);
        return (((((((((this.duration == rhs.duration)||((this.duration!= null)&&this.duration.equals(rhs.duration)))&&((this.minChargingRate == rhs.minChargingRate)||((this.minChargingRate!= null)&&this.minChargingRate.equals(rhs.minChargingRate))))&&((this.startSchedule == rhs.startSchedule)||((this.startSchedule!= null)&&this.startSchedule.equals(rhs.startSchedule))))&&((this.chargingSchedulePeriod == rhs.chargingSchedulePeriod)||((this.chargingSchedulePeriod!= null)&&this.chargingSchedulePeriod.equals(rhs.chargingSchedulePeriod))))&&((this.salesTariff == rhs.salesTariff)||((this.salesTariff!= null)&&this.salesTariff.equals(rhs.salesTariff))))&&((this.chargingRateUnit == rhs.chargingRateUnit)||((this.chargingRateUnit!= null)&&this.chargingRateUnit.equals(rhs.chargingRateUnit))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))));
    }

}


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


/**
 * Sales_ Tariff
 * urn:x-oca:ocpp:uid:2:233272
 * NOTE: This dataType is based on dataTypes from &lt;&lt;ref-ISOIEC15118-2,ISO 15118-2&gt;&gt;.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "id",
    "salesTariffDescription",
    "numEPriceLevels",
    "salesTariffEntry"
})
@Generated("jsonschema2pojo")
public class SalesTariff {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * SalesTariff identifier used to identify one sales tariff. An SAID remains a unique identifier for one schedule throughout a charging session.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Identified_ Object. MRID. Numeric_ Identifier\r\nurn:x-enexis:ecdm:uid:1:569198\r\nSalesTariff identifier used to identify one sales tariff. An SAID remains a unique identifier for one schedule throughout a charging session.\r\n")
    @NotNull
    private Integer id;
    /**
     * Sales_ Tariff. Sales. Tariff_ Description
     * urn:x-oca:ocpp:uid:1:569283
     * A human readable title/short description of the sales tariff e.g. for HMI display purposes.
     * 
     * 
     */
    @JsonProperty("salesTariffDescription")
    @JsonPropertyDescription("Sales_ Tariff. Sales. Tariff_ Description\r\nurn:x-oca:ocpp:uid:1:569283\r\nA human readable title/short description of the sales tariff e.g. for HMI display purposes.\r\n")
    @Size(max = 32)
    private String salesTariffDescription;
    /**
     * Sales_ Tariff. Num_ E_ Price_ Levels. Counter
     * urn:x-oca:ocpp:uid:1:569284
     * Defines the overall number of distinct price levels used across all provided SalesTariff elements.
     * 
     * 
     */
    @JsonProperty("numEPriceLevels")
    @JsonPropertyDescription("Sales_ Tariff. Num_ E_ Price_ Levels. Counter\r\nurn:x-oca:ocpp:uid:1:569284\r\nDefines the overall number of distinct price levels used across all provided SalesTariff elements.\r\n")
    private Integer numEPriceLevels;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("salesTariffEntry")
    @Size(min = 1, max = 1024)
    @Valid
    @NotNull
    private List<SalesTariffEntry> salesTariffEntry = new ArrayList<SalesTariffEntry>();

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

    public SalesTariff withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * SalesTariff identifier used to identify one sales tariff. An SAID remains a unique identifier for one schedule throughout a charging session.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * Identified_ Object. MRID. Numeric_ Identifier
     * urn:x-enexis:ecdm:uid:1:569198
     * SalesTariff identifier used to identify one sales tariff. An SAID remains a unique identifier for one schedule throughout a charging session.
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public SalesTariff withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Sales_ Tariff. Sales. Tariff_ Description
     * urn:x-oca:ocpp:uid:1:569283
     * A human readable title/short description of the sales tariff e.g. for HMI display purposes.
     * 
     * 
     */
    @JsonProperty("salesTariffDescription")
    public String getSalesTariffDescription() {
        return salesTariffDescription;
    }

    /**
     * Sales_ Tariff. Sales. Tariff_ Description
     * urn:x-oca:ocpp:uid:1:569283
     * A human readable title/short description of the sales tariff e.g. for HMI display purposes.
     * 
     * 
     */
    @JsonProperty("salesTariffDescription")
    public void setSalesTariffDescription(String salesTariffDescription) {
        this.salesTariffDescription = salesTariffDescription;
    }

    public SalesTariff withSalesTariffDescription(String salesTariffDescription) {
        this.salesTariffDescription = salesTariffDescription;
        return this;
    }

    /**
     * Sales_ Tariff. Num_ E_ Price_ Levels. Counter
     * urn:x-oca:ocpp:uid:1:569284
     * Defines the overall number of distinct price levels used across all provided SalesTariff elements.
     * 
     * 
     */
    @JsonProperty("numEPriceLevels")
    public Integer getNumEPriceLevels() {
        return numEPriceLevels;
    }

    /**
     * Sales_ Tariff. Num_ E_ Price_ Levels. Counter
     * urn:x-oca:ocpp:uid:1:569284
     * Defines the overall number of distinct price levels used across all provided SalesTariff elements.
     * 
     * 
     */
    @JsonProperty("numEPriceLevels")
    public void setNumEPriceLevels(Integer numEPriceLevels) {
        this.numEPriceLevels = numEPriceLevels;
    }

    public SalesTariff withNumEPriceLevels(Integer numEPriceLevels) {
        this.numEPriceLevels = numEPriceLevels;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("salesTariffEntry")
    public List<SalesTariffEntry> getSalesTariffEntry() {
        return salesTariffEntry;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("salesTariffEntry")
    public void setSalesTariffEntry(List<SalesTariffEntry> salesTariffEntry) {
        this.salesTariffEntry = salesTariffEntry;
    }

    public SalesTariff withSalesTariffEntry(List<SalesTariffEntry> salesTariffEntry) {
        this.salesTariffEntry = salesTariffEntry;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SalesTariff.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("salesTariffDescription");
        sb.append('=');
        sb.append(((this.salesTariffDescription == null)?"<null>":this.salesTariffDescription));
        sb.append(',');
        sb.append("numEPriceLevels");
        sb.append('=');
        sb.append(((this.numEPriceLevels == null)?"<null>":this.numEPriceLevels));
        sb.append(',');
        sb.append("salesTariffEntry");
        sb.append('=');
        sb.append(((this.salesTariffEntry == null)?"<null>":this.salesTariffEntry));
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
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.salesTariffEntry == null)? 0 :this.salesTariffEntry.hashCode()));
        result = ((result* 31)+((this.salesTariffDescription == null)? 0 :this.salesTariffDescription.hashCode()));
        result = ((result* 31)+((this.numEPriceLevels == null)? 0 :this.numEPriceLevels.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SalesTariff) == false) {
            return false;
        }
        SalesTariff rhs = ((SalesTariff) other);
        return ((((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.salesTariffEntry == rhs.salesTariffEntry)||((this.salesTariffEntry!= null)&&this.salesTariffEntry.equals(rhs.salesTariffEntry))))&&((this.salesTariffDescription == rhs.salesTariffDescription)||((this.salesTariffDescription!= null)&&this.salesTariffDescription.equals(rhs.salesTariffDescription))))&&((this.numEPriceLevels == rhs.numEPriceLevels)||((this.numEPriceLevels!= null)&&this.numEPriceLevels.equals(rhs.numEPriceLevels))));
    }

}

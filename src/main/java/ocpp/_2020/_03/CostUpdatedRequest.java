
package ocpp._2020._03;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "totalCost",
    "transactionId"
})
@Generated("jsonschema2pojo")
public class CostUpdatedRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Current total cost, based on the information known by the CSMS, of the transaction including taxes. In the currency configured with the configuration Variable: [&lt;&lt;configkey-currency, Currency&gt;&gt;]
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("totalCost")
    @JsonPropertyDescription("Current total cost, based on the information known by the CSMS, of the transaction including taxes. In the currency configured with the configuration Variable: [&lt;&lt;configkey-currency, Currency&gt;&gt;]\r\n\r\n")
    @NotNull
    private Double totalCost;
    /**
     * Transaction Id of the transaction the current cost are asked for.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    @JsonPropertyDescription("Transaction Id of the transaction the current cost are asked for.\r\n\r\n")
    @Size(max = 36)
    @NotNull
    private String transactionId;

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

    public CostUpdatedRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Current total cost, based on the information known by the CSMS, of the transaction including taxes. In the currency configured with the configuration Variable: [&lt;&lt;configkey-currency, Currency&gt;&gt;]
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("totalCost")
    public Double getTotalCost() {
        return totalCost;
    }

    /**
     * Current total cost, based on the information known by the CSMS, of the transaction including taxes. In the currency configured with the configuration Variable: [&lt;&lt;configkey-currency, Currency&gt;&gt;]
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("totalCost")
    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public CostUpdatedRequest withTotalCost(Double totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    /**
     * Transaction Id of the transaction the current cost are asked for.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Transaction Id of the transaction the current cost are asked for.
     * 
     * 
     * (Required)
     * 
     */
    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public CostUpdatedRequest withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CostUpdatedRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("totalCost");
        sb.append('=');
        sb.append(((this.totalCost == null)?"<null>":this.totalCost));
        sb.append(',');
        sb.append("transactionId");
        sb.append('=');
        sb.append(((this.transactionId == null)?"<null>":this.transactionId));
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
        result = ((result* 31)+((this.totalCost == null)? 0 :this.totalCost.hashCode()));
        result = ((result* 31)+((this.transactionId == null)? 0 :this.transactionId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CostUpdatedRequest) == false) {
            return false;
        }
        CostUpdatedRequest rhs = ((CostUpdatedRequest) other);
        return ((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.totalCost == rhs.totalCost)||((this.totalCost!= null)&&this.totalCost.equals(rhs.totalCost))))&&((this.transactionId == rhs.transactionId)||((this.transactionId!= null)&&this.transactionId.equals(rhs.transactionId))));
    }

}

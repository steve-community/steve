
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
import de.rwth.idsg.ocpp.jaxb.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "componentVariable",
    "requestId",
    "monitoringCriteria"
})
@Generated("jsonschema2pojo")
public class GetMonitoringReportRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    @JsonProperty("componentVariable")
    @Size(min = 1)
    @Valid
    private List<ComponentVariable> componentVariable = new ArrayList<ComponentVariable>();
    /**
     * The Id of the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    @JsonPropertyDescription("The Id of the request.\r\n")
    @NotNull
    private Integer requestId;
    /**
     * This field contains criteria for components for which a monitoring report is requested
     * 
     * 
     */
    @JsonProperty("monitoringCriteria")
    @JsonPropertyDescription("This field contains criteria for components for which a monitoring report is requested\r\n")
    @Size(min = 1, max = 3)
    @Valid
    private List<MonitoringCriterionEnum> monitoringCriteria = new ArrayList<MonitoringCriterionEnum>();

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

    public GetMonitoringReportRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    @JsonProperty("componentVariable")
    public List<ComponentVariable> getComponentVariable() {
        return componentVariable;
    }

    @JsonProperty("componentVariable")
    public void setComponentVariable(List<ComponentVariable> componentVariable) {
        this.componentVariable = componentVariable;
    }

    public GetMonitoringReportRequest withComponentVariable(List<ComponentVariable> componentVariable) {
        this.componentVariable = componentVariable;
        return this;
    }

    /**
     * The Id of the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public Integer getRequestId() {
        return requestId;
    }

    /**
     * The Id of the request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestId")
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public GetMonitoringReportRequest withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * This field contains criteria for components for which a monitoring report is requested
     * 
     * 
     */
    @JsonProperty("monitoringCriteria")
    public List<MonitoringCriterionEnum> getMonitoringCriteria() {
        return monitoringCriteria;
    }

    /**
     * This field contains criteria for components for which a monitoring report is requested
     * 
     * 
     */
    @JsonProperty("monitoringCriteria")
    public void setMonitoringCriteria(List<MonitoringCriterionEnum> monitoringCriteria) {
        this.monitoringCriteria = monitoringCriteria;
    }

    public GetMonitoringReportRequest withMonitoringCriteria(List<MonitoringCriterionEnum> monitoringCriteria) {
        this.monitoringCriteria = monitoringCriteria;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetMonitoringReportRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("componentVariable");
        sb.append('=');
        sb.append(((this.componentVariable == null)?"<null>":this.componentVariable));
        sb.append(',');
        sb.append("requestId");
        sb.append('=');
        sb.append(((this.requestId == null)?"<null>":this.requestId));
        sb.append(',');
        sb.append("monitoringCriteria");
        sb.append('=');
        sb.append(((this.monitoringCriteria == null)?"<null>":this.monitoringCriteria));
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
        result = ((result* 31)+((this.monitoringCriteria == null)? 0 :this.monitoringCriteria.hashCode()));
        result = ((result* 31)+((this.componentVariable == null)? 0 :this.componentVariable.hashCode()));
        result = ((result* 31)+((this.requestId == null)? 0 :this.requestId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GetMonitoringReportRequest) == false) {
            return false;
        }
        GetMonitoringReportRequest rhs = ((GetMonitoringReportRequest) other);
        return (((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.monitoringCriteria == rhs.monitoringCriteria)||((this.monitoringCriteria!= null)&&this.monitoringCriteria.equals(rhs.monitoringCriteria))))&&((this.componentVariable == rhs.componentVariable)||((this.componentVariable!= null)&&this.componentVariable.equals(rhs.componentVariable))))&&((this.requestId == rhs.requestId)||((this.requestId!= null)&&this.requestId.equals(rhs.requestId))));
    }

}


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
    "localAuthorizationList",
    "versionNumber",
    "updateType"
})
@Generated("jsonschema2pojo")
public class SendLocalListRequest implements RequestType
{

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    @JsonProperty("localAuthorizationList")
    @Size(min = 1)
    @Valid
    private List<AuthorizationData> localAuthorizationList = new ArrayList<AuthorizationData>();
    /**
     * In case of a full update this is the version number of the full list. In case of a differential update it is the version number of the list after the update has been applied.
     * 
     * (Required)
     * 
     */
    @JsonProperty("versionNumber")
    @JsonPropertyDescription("In case of a full update this is the version number of the full list. In case of a differential update it is the version number of the list after the update has been applied.\r\n")
    @NotNull
    private Integer versionNumber;
    /**
     * This contains the type of update (full or differential) of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("updateType")
    @JsonPropertyDescription("This contains the type of update (full or differential) of this request.\r\n")
    @NotNull
    private UpdateEnum updateType;

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

    public SendLocalListRequest withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    @JsonProperty("localAuthorizationList")
    public List<AuthorizationData> getLocalAuthorizationList() {
        return localAuthorizationList;
    }

    @JsonProperty("localAuthorizationList")
    public void setLocalAuthorizationList(List<AuthorizationData> localAuthorizationList) {
        this.localAuthorizationList = localAuthorizationList;
    }

    public SendLocalListRequest withLocalAuthorizationList(List<AuthorizationData> localAuthorizationList) {
        this.localAuthorizationList = localAuthorizationList;
        return this;
    }

    /**
     * In case of a full update this is the version number of the full list. In case of a differential update it is the version number of the list after the update has been applied.
     * 
     * (Required)
     * 
     */
    @JsonProperty("versionNumber")
    public Integer getVersionNumber() {
        return versionNumber;
    }

    /**
     * In case of a full update this is the version number of the full list. In case of a differential update it is the version number of the list after the update has been applied.
     * 
     * (Required)
     * 
     */
    @JsonProperty("versionNumber")
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public SendLocalListRequest withVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    /**
     * This contains the type of update (full or differential) of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("updateType")
    public UpdateEnum getUpdateType() {
        return updateType;
    }

    /**
     * This contains the type of update (full or differential) of this request.
     * 
     * (Required)
     * 
     */
    @JsonProperty("updateType")
    public void setUpdateType(UpdateEnum updateType) {
        this.updateType = updateType;
    }

    public SendLocalListRequest withUpdateType(UpdateEnum updateType) {
        this.updateType = updateType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SendLocalListRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("localAuthorizationList");
        sb.append('=');
        sb.append(((this.localAuthorizationList == null)?"<null>":this.localAuthorizationList));
        sb.append(',');
        sb.append("versionNumber");
        sb.append('=');
        sb.append(((this.versionNumber == null)?"<null>":this.versionNumber));
        sb.append(',');
        sb.append("updateType");
        sb.append('=');
        sb.append(((this.updateType == null)?"<null>":this.updateType));
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
        result = ((result* 31)+((this.localAuthorizationList == null)? 0 :this.localAuthorizationList.hashCode()));
        result = ((result* 31)+((this.versionNumber == null)? 0 :this.versionNumber.hashCode()));
        result = ((result* 31)+((this.updateType == null)? 0 :this.updateType.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SendLocalListRequest) == false) {
            return false;
        }
        SendLocalListRequest rhs = ((SendLocalListRequest) other);
        return (((((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData)))&&((this.localAuthorizationList == rhs.localAuthorizationList)||((this.localAuthorizationList!= null)&&this.localAuthorizationList.equals(rhs.localAuthorizationList))))&&((this.versionNumber == rhs.versionNumber)||((this.versionNumber!= null)&&this.versionNumber.equals(rhs.versionNumber))))&&((this.updateType == rhs.updateType)||((this.updateType!= null)&&this.updateType.equals(rhs.updateType))));
    }

}

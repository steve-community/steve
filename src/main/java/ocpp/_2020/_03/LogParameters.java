
package ocpp._2020._03;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.joda.time.DateTime;


/**
 * Log
 * urn:x-enexis:ecdm:uid:2:233373
 * Generic class for the configuration of logging entries.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "remoteLocation",
    "oldestTimestamp",
    "latestTimestamp"
})
public class LogParameters {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * Log. Remote_ Location. URI
     * urn:x-enexis:ecdm:uid:1:569484
     * The URL of the location at the remote system where the log should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("remoteLocation")
    @JsonPropertyDescription("Log. Remote_ Location. URI\r\nurn:x-enexis:ecdm:uid:1:569484\r\nThe URL of the location at the remote system where the log should be stored.\r\n")
    @Size(max = 512)
    @NotNull
    private String remoteLocation;
    /**
     * Log. Oldest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569477
     * This contains the date and time of the oldest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("oldestTimestamp")
    @JsonPropertyDescription("Log. Oldest_ Timestamp. Date_ Time\r\nurn:x-enexis:ecdm:uid:1:569477\r\nThis contains the date and time of the oldest logging information to include in the diagnostics.\r\n")
    private DateTime oldestTimestamp;
    /**
     * Log. Latest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569482
     * This contains the date and time of the latest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("latestTimestamp")
    @JsonPropertyDescription("Log. Latest_ Timestamp. Date_ Time\r\nurn:x-enexis:ecdm:uid:1:569482\r\nThis contains the date and time of the latest logging information to include in the diagnostics.\r\n")
    private DateTime latestTimestamp;

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

    public LogParameters withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * Log. Remote_ Location. URI
     * urn:x-enexis:ecdm:uid:1:569484
     * The URL of the location at the remote system where the log should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("remoteLocation")
    public String getRemoteLocation() {
        return remoteLocation;
    }

    /**
     * Log. Remote_ Location. URI
     * urn:x-enexis:ecdm:uid:1:569484
     * The URL of the location at the remote system where the log should be stored.
     * 
     * (Required)
     * 
     */
    @JsonProperty("remoteLocation")
    public void setRemoteLocation(String remoteLocation) {
        this.remoteLocation = remoteLocation;
    }

    public LogParameters withRemoteLocation(String remoteLocation) {
        this.remoteLocation = remoteLocation;
        return this;
    }

    /**
     * Log. Oldest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569477
     * This contains the date and time of the oldest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("oldestTimestamp")
    public DateTime getOldestTimestamp() {
        return oldestTimestamp;
    }

    /**
     * Log. Oldest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569477
     * This contains the date and time of the oldest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("oldestTimestamp")
    public void setOldestTimestamp(DateTime oldestTimestamp) {
        this.oldestTimestamp = oldestTimestamp;
    }

    public LogParameters withOldestTimestamp(DateTime oldestTimestamp) {
        this.oldestTimestamp = oldestTimestamp;
        return this;
    }

    /**
     * Log. Latest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569482
     * This contains the date and time of the latest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("latestTimestamp")
    public DateTime getLatestTimestamp() {
        return latestTimestamp;
    }

    /**
     * Log. Latest_ Timestamp. Date_ Time
     * urn:x-enexis:ecdm:uid:1:569482
     * This contains the date and time of the latest logging information to include in the diagnostics.
     * 
     * 
     */
    @JsonProperty("latestTimestamp")
    public void setLatestTimestamp(DateTime latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    public LogParameters withLatestTimestamp(DateTime latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogParameters.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("remoteLocation");
        sb.append('=');
        sb.append(((this.remoteLocation == null)?"<null>":this.remoteLocation));
        sb.append(',');
        sb.append("oldestTimestamp");
        sb.append('=');
        sb.append(((this.oldestTimestamp == null)?"<null>":this.oldestTimestamp));
        sb.append(',');
        sb.append("latestTimestamp");
        sb.append('=');
        sb.append(((this.latestTimestamp == null)?"<null>":this.latestTimestamp));
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
        result = ((result* 31)+((this.remoteLocation == null)? 0 :this.remoteLocation.hashCode()));
        result = ((result* 31)+((this.oldestTimestamp == null)? 0 :this.oldestTimestamp.hashCode()));
        result = ((result* 31)+((this.latestTimestamp == null)? 0 :this.latestTimestamp.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LogParameters) == false) {
            return false;
        }
        LogParameters rhs = ((LogParameters) other);
        return (((((this.remoteLocation == rhs.remoteLocation)||((this.remoteLocation!= null)&&this.remoteLocation.equals(rhs.remoteLocation)))&&((this.oldestTimestamp == rhs.oldestTimestamp)||((this.oldestTimestamp!= null)&&this.oldestTimestamp.equals(rhs.oldestTimestamp))))&&((this.latestTimestamp == rhs.latestTimestamp)||((this.latestTimestamp!= null)&&this.latestTimestamp.equals(rhs.latestTimestamp))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))));
    }

}

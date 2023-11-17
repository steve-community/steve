
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
 * ID_ Token
 * urn:x-oca:ocpp:uid:2:233247
 * Contains status information about an identifier.
 * It is advised to not stop charging for a token that expires during charging, as ExpiryDate is only used for caching purposes. If ExpiryDate is not given, the status has no end date.
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customData",
    "status",
    "cacheExpiryDateTime",
    "chargingPriority",
    "language1",
    "evseId",
    "groupIdToken",
    "language2",
    "personalMessage"
})
@Generated("jsonschema2pojo")
public class IdTokenInfo {

    /**
     * This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.
     * 
     */
    @JsonProperty("customData")
    @JsonPropertyDescription("This class does not get 'AdditionalProperties = false' in the schema generation, so it can be extended with arbitrary JSON properties to allow adding custom data.")
    @Valid
    private CustomData customData;
    /**
     * ID_ Token. Status. Authorization_ Status
     * urn:x-oca:ocpp:uid:1:569372
     * Current status of the ID Token.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("ID_ Token. Status. Authorization_ Status\r\nurn:x-oca:ocpp:uid:1:569372\r\nCurrent status of the ID Token.\r\n")
    @NotNull
    private AuthorizationStatusEnum status;
    /**
     * ID_ Token. Expiry. Date_ Time
     * urn:x-oca:ocpp:uid:1:569373
     * Date and Time after which the token must be considered invalid.
     * 
     * 
     */
    @JsonProperty("cacheExpiryDateTime")
    @JsonPropertyDescription("ID_ Token. Expiry. Date_ Time\r\nurn:x-oca:ocpp:uid:1:569373\r\nDate and Time after which the token must be considered invalid.\r\n")
    private DateTime cacheExpiryDateTime;
    /**
     * Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; overrules this one. 
     * 
     * 
     */
    @JsonProperty("chargingPriority")
    @JsonPropertyDescription("Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; overrules this one. \r\n")
    private Integer chargingPriority;
    /**
     * ID_ Token. Language1. Language_ Code
     * urn:x-oca:ocpp:uid:1:569374
     * Preferred user interface language of identifier user. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     * 
     */
    @JsonProperty("language1")
    @JsonPropertyDescription("ID_ Token. Language1. Language_ Code\r\nurn:x-oca:ocpp:uid:1:569374\r\nPreferred user interface language of identifier user. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.\r\n\r\n")
    @Size(max = 8)
    private String language1;
    /**
     * Only used when the IdToken is only valid for one or more specific EVSEs, not for the entire Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    @JsonPropertyDescription("Only used when the IdToken is only valid for one or more specific EVSEs, not for the entire Charging Station.\r\n\r\n")
    @Size(min = 1)
    @Valid
    private List<Integer> evseId = new ArrayList<Integer>();
    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("groupIdToken")
    @JsonPropertyDescription("Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.\r\n")
    @Valid
    private IdToken groupIdToken;
    /**
     * ID_ Token. Language2. Language_ Code
     * urn:x-oca:ocpp:uid:1:569375
     * Second preferred user interface language of identifier user. Don’t use when language1 is omitted, has to be different from language1. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language2")
    @JsonPropertyDescription("ID_ Token. Language2. Language_ Code\r\nurn:x-oca:ocpp:uid:1:569375\r\nSecond preferred user interface language of identifier user. Don\u2019t use when language1 is omitted, has to be different from language1. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.\r\n")
    @Size(max = 8)
    private String language2;
    /**
     * Message_ Content
     * urn:x-enexis:ecdm:uid:2:234490
     * Contains message details, for a message to be displayed on a Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("personalMessage")
    @JsonPropertyDescription("Message_ Content\r\nurn:x-enexis:ecdm:uid:2:234490\r\nContains message details, for a message to be displayed on a Charging Station.\r\n\r\n")
    @Valid
    private MessageContent personalMessage;

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

    public IdTokenInfo withCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }

    /**
     * ID_ Token. Status. Authorization_ Status
     * urn:x-oca:ocpp:uid:1:569372
     * Current status of the ID Token.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public AuthorizationStatusEnum getStatus() {
        return status;
    }

    /**
     * ID_ Token. Status. Authorization_ Status
     * urn:x-oca:ocpp:uid:1:569372
     * Current status of the ID Token.
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(AuthorizationStatusEnum status) {
        this.status = status;
    }

    public IdTokenInfo withStatus(AuthorizationStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * ID_ Token. Expiry. Date_ Time
     * urn:x-oca:ocpp:uid:1:569373
     * Date and Time after which the token must be considered invalid.
     * 
     * 
     */
    @JsonProperty("cacheExpiryDateTime")
    public DateTime getCacheExpiryDateTime() {
        return cacheExpiryDateTime;
    }

    /**
     * ID_ Token. Expiry. Date_ Time
     * urn:x-oca:ocpp:uid:1:569373
     * Date and Time after which the token must be considered invalid.
     * 
     * 
     */
    @JsonProperty("cacheExpiryDateTime")
    public void setCacheExpiryDateTime(DateTime cacheExpiryDateTime) {
        this.cacheExpiryDateTime = cacheExpiryDateTime;
    }

    public IdTokenInfo withCacheExpiryDateTime(DateTime cacheExpiryDateTime) {
        this.cacheExpiryDateTime = cacheExpiryDateTime;
        return this;
    }

    /**
     * Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; overrules this one. 
     * 
     * 
     */
    @JsonProperty("chargingPriority")
    public Integer getChargingPriority() {
        return chargingPriority;
    }

    /**
     * Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; overrules this one. 
     * 
     * 
     */
    @JsonProperty("chargingPriority")
    public void setChargingPriority(Integer chargingPriority) {
        this.chargingPriority = chargingPriority;
    }

    public IdTokenInfo withChargingPriority(Integer chargingPriority) {
        this.chargingPriority = chargingPriority;
        return this;
    }

    /**
     * ID_ Token. Language1. Language_ Code
     * urn:x-oca:ocpp:uid:1:569374
     * Preferred user interface language of identifier user. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     * 
     */
    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    /**
     * ID_ Token. Language1. Language_ Code
     * urn:x-oca:ocpp:uid:1:569374
     * Preferred user interface language of identifier user. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     * 
     */
    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    public IdTokenInfo withLanguage1(String language1) {
        this.language1 = language1;
        return this;
    }

    /**
     * Only used when the IdToken is only valid for one or more specific EVSEs, not for the entire Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    public List<Integer> getEvseId() {
        return evseId;
    }

    /**
     * Only used when the IdToken is only valid for one or more specific EVSEs, not for the entire Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("evseId")
    public void setEvseId(List<Integer> evseId) {
        this.evseId = evseId;
    }

    public IdTokenInfo withEvseId(List<Integer> evseId) {
        this.evseId = evseId;
        return this;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("groupIdToken")
    public IdToken getGroupIdToken() {
        return groupIdToken;
    }

    /**
     * Contains a case insensitive identifier to use for the authorization and the type of authorization to support multiple forms of identifiers.
     * 
     * 
     */
    @JsonProperty("groupIdToken")
    public void setGroupIdToken(IdToken groupIdToken) {
        this.groupIdToken = groupIdToken;
    }

    public IdTokenInfo withGroupIdToken(IdToken groupIdToken) {
        this.groupIdToken = groupIdToken;
        return this;
    }

    /**
     * ID_ Token. Language2. Language_ Code
     * urn:x-oca:ocpp:uid:1:569375
     * Second preferred user interface language of identifier user. Don’t use when language1 is omitted, has to be different from language1. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    /**
     * ID_ Token. Language2. Language_ Code
     * urn:x-oca:ocpp:uid:1:569375
     * Second preferred user interface language of identifier user. Don’t use when language1 is omitted, has to be different from language1. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;.
     * 
     * 
     */
    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    public IdTokenInfo withLanguage2(String language2) {
        this.language2 = language2;
        return this;
    }

    /**
     * Message_ Content
     * urn:x-enexis:ecdm:uid:2:234490
     * Contains message details, for a message to be displayed on a Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("personalMessage")
    public MessageContent getPersonalMessage() {
        return personalMessage;
    }

    /**
     * Message_ Content
     * urn:x-enexis:ecdm:uid:2:234490
     * Contains message details, for a message to be displayed on a Charging Station.
     * 
     * 
     * 
     */
    @JsonProperty("personalMessage")
    public void setPersonalMessage(MessageContent personalMessage) {
        this.personalMessage = personalMessage;
    }

    public IdTokenInfo withPersonalMessage(MessageContent personalMessage) {
        this.personalMessage = personalMessage;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(IdTokenInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customData");
        sb.append('=');
        sb.append(((this.customData == null)?"<null>":this.customData));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("cacheExpiryDateTime");
        sb.append('=');
        sb.append(((this.cacheExpiryDateTime == null)?"<null>":this.cacheExpiryDateTime));
        sb.append(',');
        sb.append("chargingPriority");
        sb.append('=');
        sb.append(((this.chargingPriority == null)?"<null>":this.chargingPriority));
        sb.append(',');
        sb.append("language1");
        sb.append('=');
        sb.append(((this.language1 == null)?"<null>":this.language1));
        sb.append(',');
        sb.append("evseId");
        sb.append('=');
        sb.append(((this.evseId == null)?"<null>":this.evseId));
        sb.append(',');
        sb.append("groupIdToken");
        sb.append('=');
        sb.append(((this.groupIdToken == null)?"<null>":this.groupIdToken));
        sb.append(',');
        sb.append("language2");
        sb.append('=');
        sb.append(((this.language2 == null)?"<null>":this.language2));
        sb.append(',');
        sb.append("personalMessage");
        sb.append('=');
        sb.append(((this.personalMessage == null)?"<null>":this.personalMessage));
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
        result = ((result* 31)+((this.evseId == null)? 0 :this.evseId.hashCode()));
        result = ((result* 31)+((this.language2 == null)? 0 :this.language2 .hashCode()));
        result = ((result* 31)+((this.language1 == null)? 0 :this.language1 .hashCode()));
        result = ((result* 31)+((this.cacheExpiryDateTime == null)? 0 :this.cacheExpiryDateTime.hashCode()));
        result = ((result* 31)+((this.customData == null)? 0 :this.customData.hashCode()));
        result = ((result* 31)+((this.chargingPriority == null)? 0 :this.chargingPriority.hashCode()));
        result = ((result* 31)+((this.personalMessage == null)? 0 :this.personalMessage.hashCode()));
        result = ((result* 31)+((this.groupIdToken == null)? 0 :this.groupIdToken.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof IdTokenInfo) == false) {
            return false;
        }
        IdTokenInfo rhs = ((IdTokenInfo) other);
        return ((((((((((this.evseId == rhs.evseId)||((this.evseId!= null)&&this.evseId.equals(rhs.evseId)))&&((this.language2 == rhs.language2)||((this.language2 != null)&&this.language2 .equals(rhs.language2))))&&((this.language1 == rhs.language1)||((this.language1 != null)&&this.language1 .equals(rhs.language1))))&&((this.cacheExpiryDateTime == rhs.cacheExpiryDateTime)||((this.cacheExpiryDateTime!= null)&&this.cacheExpiryDateTime.equals(rhs.cacheExpiryDateTime))))&&((this.customData == rhs.customData)||((this.customData!= null)&&this.customData.equals(rhs.customData))))&&((this.chargingPriority == rhs.chargingPriority)||((this.chargingPriority!= null)&&this.chargingPriority.equals(rhs.chargingPriority))))&&((this.personalMessage == rhs.personalMessage)||((this.personalMessage!= null)&&this.personalMessage.equals(rhs.personalMessage))))&&((this.groupIdToken == rhs.groupIdToken)||((this.groupIdToken!= null)&&this.groupIdToken.equals(rhs.groupIdToken))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}

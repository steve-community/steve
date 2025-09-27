package de.rwth.idsg.steve.gateway.ocpi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignedData {
    @JsonProperty("encoding_method")
    private String encodingMethod;
    
    @JsonProperty("encoding_method_version")
    private Integer encodingMethodVersion;
    
    @JsonProperty("public_key")
    private String publicKey;
    
    @JsonProperty("signed_values")
    private String[] signedValues;
    
    private String url;
}

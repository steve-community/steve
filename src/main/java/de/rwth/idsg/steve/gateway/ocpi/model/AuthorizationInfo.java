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
public class AuthorizationInfo {
    private String allowed;
    private Token token;
    private LocationReferences location;
    
    @JsonProperty("authorization_reference")
    private String authorizationReference;
    
    private String info;
}

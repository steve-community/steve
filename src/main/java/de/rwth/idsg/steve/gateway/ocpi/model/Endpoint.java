package de.rwth.idsg.steve.gateway.ocpi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {
    private String identifier;
    private String role;
    private String url;
}

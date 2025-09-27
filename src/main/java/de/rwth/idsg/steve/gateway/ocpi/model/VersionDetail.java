package de.rwth.idsg.steve.gateway.ocpi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDetail {
    private String version;
    private List<Endpoint> endpoints;
}

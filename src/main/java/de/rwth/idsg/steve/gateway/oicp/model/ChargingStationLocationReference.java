package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationLocationReference {
    private String evseId;
    private String chargingStationId;
    private String chargingPoolId;
    private String chargingStationLocationReference;
}
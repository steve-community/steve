package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingFacility {
    private String powerType;
    private BigDecimal power;
    private BigDecimal voltage;
    private BigDecimal amperage;
    private String chargingModes;
}
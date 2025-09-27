package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterValue {
    private BigDecimal value;
    private String unit;
    private LocalDateTime timestamp;
    private String typeOfMeterValue;
}
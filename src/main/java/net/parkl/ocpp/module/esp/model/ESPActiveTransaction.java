package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPActiveTransaction implements Serializable {
    private String ocppChargingProcessId;
    private LocalDateTime startDate;
    private String startValue;
    private String ocppTag;
}

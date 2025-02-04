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
public class ESPClosedTransaction implements Serializable {
    private String ocppChargingProcessId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String startValue;
    private String stopValue;
    private String ocppTag;
    private String stopReason;
}

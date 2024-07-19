package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPClosedTransaction implements Serializable {
    private String ocppChargingProcessId;
    private Date startDate;
    private Date endDate;
    private String startValue;
    private String stopValue;
    private String ocppTag;
    private String stopReason;
}

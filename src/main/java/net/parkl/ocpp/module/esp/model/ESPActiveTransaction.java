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
public class ESPActiveTransaction implements Serializable {
    private String ocppChargingProcessId;
    private Date startDate;
    private String startValue;
    private String ocppTag;
}

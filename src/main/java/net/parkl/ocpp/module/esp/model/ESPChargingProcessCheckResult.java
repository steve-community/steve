package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPChargingProcessCheckResult implements Serializable {
    private boolean exists;
    private boolean stopped;
}

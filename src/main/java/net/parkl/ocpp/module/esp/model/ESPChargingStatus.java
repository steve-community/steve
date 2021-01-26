package net.parkl.ocpp.module.esp.model;

import lombok.*;

@Getter
@Setter
@Builder
public class ESPChargingStatus {
    private float throughputPower;
    private float actualCurrent;

}

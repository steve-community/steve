package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ESPChargerStatus {
    private String externalLotId;
    private String externalChargerId;
    private ESPChargerState state;

}

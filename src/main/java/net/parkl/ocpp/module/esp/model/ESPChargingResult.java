package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ESPChargingResult {
    private ESPChargingData chargingData;
    private String errorCode;
    private Boolean stoppedWithoutTransaction;


}

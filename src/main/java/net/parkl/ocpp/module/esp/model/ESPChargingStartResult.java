package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ESPChargingStartResult {
    private String externalChargingProcessId;
    private String errorCode;
    private String chargerState;

}

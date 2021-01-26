package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ESPChargingStartRequest {
    private String chargeBoxId;

    private String chargerId;
    private String licensePlate;
    private Float limitKwh;
    private Integer limitMin;

}

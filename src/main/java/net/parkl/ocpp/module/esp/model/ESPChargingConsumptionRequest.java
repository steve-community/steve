package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ESPChargingConsumptionRequest {
    private String externalChargeId;
    private float totalPower;
    private Float startValue;
    private Float stopValue;

    private Date start;
    private Date end;

}

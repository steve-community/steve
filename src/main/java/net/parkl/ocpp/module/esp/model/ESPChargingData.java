package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
public class ESPChargingData {
    private Date start;
    private Date end;
    private float totalPower;
    private Float startValue;
    private Float stopValue;

}

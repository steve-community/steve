package net.parkl.ocpp.service.cs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class ChargingMeterValueDto {
    private Date valueTimestamp;
    private String energy;
    private String power;
    private String energyUnit;
    private String powerUnit;
    private String soc;
}
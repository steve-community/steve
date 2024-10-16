package net.parkl.ocpp.service.cs;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChargingMeterValueDto {
    private Date valueTimestamp;
    private String energy;
    private String power;
    private String energyUnit;
    private String powerUnit;
    private String soc;
}
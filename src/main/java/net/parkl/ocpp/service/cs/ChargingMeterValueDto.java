package net.parkl.ocpp.service.cs;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChargingMeterValueDto {
    private LocalDateTime valueTimestamp;
    private String energy;
    private String power;
    private String energyUnit;
    private String powerUnit;
    private String soc;
}

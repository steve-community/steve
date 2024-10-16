package net.parkl.ocpp.service.cs;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChargingMeterValueDtoList {
    List<ChargingMeterValueDto> meterValues;
}

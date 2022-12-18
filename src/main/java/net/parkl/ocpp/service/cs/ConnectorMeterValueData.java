package net.parkl.ocpp.service.cs;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ConnectorMeterValueData implements Serializable {
    private String unit;
    private String value;
}

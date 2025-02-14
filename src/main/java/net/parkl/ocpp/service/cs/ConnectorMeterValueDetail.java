package net.parkl.ocpp.service.cs;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ConnectorMeterValueDetail implements Serializable {
    private LocalDateTime valueTimestamp;
    private String value;
    private String readingContext;
    private String format;
    private String measurand;
    private String location;
    private String unit;
    private String phase;
}

package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPConnectorStatus implements Serializable {
    private int connectorId;
    private String errorCode;
    private String errorInfo;
    private String status;

    private LocalDateTime statusTimestamp;

    private String vendorErrorCode;

    private String vendorId;
}

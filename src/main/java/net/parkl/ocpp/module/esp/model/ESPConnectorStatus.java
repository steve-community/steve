package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPConnectorStatus implements Serializable {
    private String errorCode;
    private String errorInfo;
    private String status;

    private Date statusTimestamp;

    private String vendorErrorCode;

    private String vendorId;
}

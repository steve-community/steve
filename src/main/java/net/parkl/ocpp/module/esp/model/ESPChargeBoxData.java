package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPChargeBoxData implements Serializable {
    private String endpointAddress;
    private String ocppProtocol;
    private String chargePointVendor;
    private String chargePointModel;
    private String chargePointSerialNumber;
    private String chargeBoxSerialNumber;
    private String fwVersion;
    private String meterType;
    private String meterSerialNumber;
    private Date lastHeartbeatTimestamp;

}

package net.parkl.ocpp.module.esp.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ESPRfidChargingStartRequest implements Serializable {
    private String rfidTag;
    private int connectorId;
    private String chargeBoxId;
    private String externalChargingProcessId;
}

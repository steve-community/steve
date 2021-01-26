package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ESPChargeBoxConfiguration {
    private String key;
    private String value;
    private boolean readOnly;
}

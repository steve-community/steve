package net.parkl.ocpp.module.esp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ESPChargerStatusResult {
    private List<ESPChargerStatus> status;
    private String errorCode;
}

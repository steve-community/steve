package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPMeterValues implements Serializable {
    private float activePowerImport;
    private float totalEnergyImport;
    private Float soc;


}

package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identification {
    private String rfidId;
    private String qrCodeIdentification;
    private String plugAndChargeIdentification;
    private String remoteIdentification;
    private String contractId;
}
package de.rwth.idsg.steve.ocpp.ws.cluster;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClusteredInvocationRequest implements Serializable {
    private String chargeBoxId;
    private String payload;
    private String responseClassName;
    private String callerIp;
}

package de.rwth.idsg.steve.ocpp.ws.cluster;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClusteredInvocationCallback {
    private String chargeBoxId;
    private String payload;
}

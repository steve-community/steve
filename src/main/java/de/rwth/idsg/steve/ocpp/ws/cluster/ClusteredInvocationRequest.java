package de.rwth.idsg.steve.ocpp.ws.cluster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClusteredInvocationRequest implements Serializable {
    private String requestType;
    private String request;
}

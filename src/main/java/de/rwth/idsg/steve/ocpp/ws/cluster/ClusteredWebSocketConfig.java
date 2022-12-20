package de.rwth.idsg.steve.ocpp.ws.cluster;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClusteredWebSocketConfig {
    @Value("${ocpp.ws.session.clustered.enabled:false}")
    @Getter
    private boolean clusteredWebSocketSessionEnabled;
}

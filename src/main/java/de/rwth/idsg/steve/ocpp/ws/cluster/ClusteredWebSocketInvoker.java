package de.rwth.idsg.steve.ocpp.ws.cluster;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.ws.ChargePointServiceInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClusteredWebSocketInvoker {
    private final ClusteredWebSocketConfig clusteredWebSocketConfig;
    private final ClusteredInvokerClient clusteredInvokerClient;

    public void runPipeline(ChargePointServiceInvoker wsHelper, ChargePointSelect cp, CommunicationTask task) {
        if (isLocal(wsHelper, cp)) {
            wsHelper.runPipeline(cp, task);
        } else {
            clusteredInvokerClient.invoke(cp.getChargeBoxId(), task.getParams());
        }
    }

    private boolean isLocal(ChargePointServiceInvoker wsHelper, ChargePointSelect cp) {
        if (!clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            return true;
        }
        return wsHelper.getEndpoint().containsLocalSession(cp.getChargeBoxId());
    }
}

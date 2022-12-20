package de.rwth.idsg.steve.ocpp.ws.cluster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.service.cluster.WebSocketClusterSessionService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClusteredWebSocketSessionStore {
    private final ClusteredWebSocketConfig clusteredWebSocketConfig;
    private final WebSocketClusterSessionService webSocketClusterSessionService;

    public boolean isClusteredSessionEnabled() {
        return clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled();
    }

    public void add(String id, String chargeBoxId) {
        if (clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            log.info("Adding clustered session {} for charge box: {}", id, chargeBoxId);
            webSocketClusterSessionService.addSession(id, chargeBoxId, System.getenv("POD_IP"));
        }
    }

    public void remove(String id) {
        if (clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            log.info("Removing clustered session: {}", id);
            webSocketClusterSessionService.removeSession(id);
        }
    }

    public List<String> getChargeBoxIds() {
        return webSocketClusterSessionService.getAllChargeBoxIds();
    }

    public String getChargeBoxIp(String chargeBoxId) {
        return webSocketClusterSessionService.findLastIpByChargeBoxId(chargeBoxId);
    }
}

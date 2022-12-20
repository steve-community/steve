package net.parkl.ocpp.service.cluster;

import lombok.RequiredArgsConstructor;
import net.parkl.ocpp.entities.WebSocketClusterSession;
import net.parkl.ocpp.repositories.WebSocketClusterSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketClusterSessionService {
    private final WebSocketClusterSessionRepository clusterSessionRepository;

    @Transactional
    public void removeSession(String id) {
        clusterSessionRepository.deleteById(id);
    }

    public void addSession(String id, String chargeBoxId, String podIp) {
        WebSocketClusterSession session = new WebSocketClusterSession();
        session.setSessionId(id);
        session.setChargeBoxId(chargeBoxId);
        session.setPodIp(podIp);
        clusterSessionRepository.save(session);
    }

    public List<String> getAllChargeBoxIds() {
        return clusterSessionRepository.findAllChargeBoxIds();
    }

    public String findLastIpByChargeBoxId(String chargeBoxId) {
        List<String> podIps = clusterSessionRepository.findPodIpByChargeBoxId(chargeBoxId, PageRequest.of(0,1));
        if (podIps.isEmpty()) {
            throw new IllegalStateException("No pod IP found for charge box: "+chargeBoxId);
        }
        return podIps.get(0);
     }
}

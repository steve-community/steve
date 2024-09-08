package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppRemoteStart;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppRemoteStartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteStartService {
    private final OcppRemoteStartRepository remoteStartRepository;
    private final ConnectorRepository connectorRepo;

    public final static int REMOTE_START_VALIDITY_SECS = 5;

    @Transactional
    public void remoteStartRequested(String chargeBoxId, int connectorId, String idTag) {
        log.info("Remote start requested for chargeBoxId: {}, connectorId: {}, idTag: {}", chargeBoxId, connectorId, idTag);
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }


        OcppRemoteStart remoteStart = new OcppRemoteStart();
        remoteStart.setConnector(c);
        remoteStart.setOcppTag(idTag);
        remoteStartRepository.save(remoteStart);
    }

    @Transactional
    public void remoteStartClosed(String chargeBoxId, int connectorId, String idTag) {
        log.info("Remote start closed for chargeBoxId: {}, connectorId: {}, idTag: {}", chargeBoxId, connectorId, idTag);
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }

        remoteStartRepository.deleteByConnectorAndOcppTag(c, idTag);
    }

    public boolean hasOpenRemoteStart(String chargeBoxId, int connectorId, String idTag) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return remoteStartRepository.coundByConnectorAndOcppTagAfter(c, idTag,
                new Date(System.currentTimeMillis() - REMOTE_START_VALIDITY_SECS*1000)) > 0;
    }
}

package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppRemoteStart;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppRemoteStartRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static net.parkl.ocpp.service.ErrorMessages.INVALID_CHARGE_BOX_ID_CONNECTOR_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteStartService {
    private final OcppRemoteStartRepository remoteStartRepository;
    private final ConnectorRepository connectorRepo;
    private final AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;

    private final static int DEFAULT_REMOTE_START_VALIDITY_SECS = 60;

    @Transactional
    public void remoteStartRequested(String chargeBoxId, int connectorId, String idTag) {
        log.info("Remote start requested for chargeBoxId: {}, connectorId: {}, idTag: {}", chargeBoxId, connectorId, idTag);
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException(INVALID_CHARGE_BOX_ID_CONNECTOR_ID + chargeBoxId + "/" + connectorId);
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
            throw new IllegalStateException(INVALID_CHARGE_BOX_ID_CONNECTOR_ID + chargeBoxId + "/" + connectorId);
        }

        remoteStartRepository.deleteByConnectorAndOcppTag(c, idTag);
    }

    public boolean hasOpenRemoteStart(String chargeBoxId, int connectorId, String idTag) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException(INVALID_CHARGE_BOX_ID_CONNECTOR_ID + chargeBoxId + "/" + connectorId);
        }
        return remoteStartRepository.countByConnectorAndOcppTagAfter(c, idTag,
                getRemoteStartValidityThreshold(chargeBoxId)) > 0;
    }

    public boolean hasOpenRemoteStart(String chargeBoxId, String idTag) {
        List<Connector> connectors = connectorRepo.findByChargeBoxId(chargeBoxId);
        long count = remoteStartRepository
                .countByConnectorsAndOcppTagAfter(connectors, idTag, getRemoteStartValidityThreshold(chargeBoxId));
        return count > 0;
    }


    public Date getRemoteStartValidityThreshold(String chargeBoxId) {
        return new Date(System.currentTimeMillis() - getRemoteStartValiditySecs(chargeBoxId) * 1000L);
    }

    private int getRemoteStartValiditySecs(String chargeBoxId) {
        int startTimeoutSecs = advancedChargeBoxConfiguration.getStartTimeoutSecs(chargeBoxId);
        if (startTimeoutSecs > 0) {
            return startTimeoutSecs;
        }
        return DEFAULT_REMOTE_START_VALIDITY_SECS;
    }
}

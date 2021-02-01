package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import net.parkl.ocpp.entities.Connector;

import java.util.Optional;

public interface ConnectorService {
    void insertConnectorStatus(InsertConnectorStatusParams params);

    Connector createConnectorIfNotExists(String chargeBoxId, int connectorId);

    Optional<Connector> findById(int connectorId);
}

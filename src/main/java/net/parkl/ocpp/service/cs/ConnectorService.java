package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import net.parkl.ocpp.entities.Connector;

import java.util.Optional;

public interface ConnectorService {
    void insertConnectorStatus(InsertConnectorStatusParams params);

    Optional<Connector> findById(int connectorId);
}

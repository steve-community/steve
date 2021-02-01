package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.TransactionStatusUpdate;
import net.parkl.ocpp.entities.Connector;
import org.joda.time.DateTime;

import java.util.Optional;

public interface ConnectorService {
    void insertConnectorStatus(InsertConnectorStatusParams params);

    Connector createConnectorIfNotExists(String chargeBoxId, int connectorId);

    Optional<Connector> findById(int connectorId);

    void createConnectorStatus(Connector connector, DateTime startTimestamp, TransactionStatusUpdate statusUpdate);
}

package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;

public interface ConnectorService {
    void insertConnectorStatus(InsertConnectorStatusParams params);

}

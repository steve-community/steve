package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import ocpp.cs._2015._10.RegistrationStatus;

import java.util.Optional;

public interface OcppNotificationService {

    void ocppStationBooted(String chargeBoxId, Optional<RegistrationStatus> status);

    void ocppStationWebSocketConnected(String chargeBoxId);

    void ocppStationWebSocketDisconnected(String chargeBoxId);

    void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode);

    void ocppTransactionStarted(int transactionId, InsertTransactionParams params);

    void ocppTransactionEnded(UpdateTransactionParams params);
}

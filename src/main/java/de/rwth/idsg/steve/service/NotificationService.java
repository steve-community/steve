package de.rwth.idsg.steve.service;

import ocpp.cs._2015._10.RegistrationStatus;

import java.util.Optional;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
public interface NotificationService {
    void ocppStationBooted(String chargeBoxId, Optional<RegistrationStatus> status);
    void ocppStationWebSocketConnected(String chargeBoxId);
    void ocppStationWebSocketDisconnected(String chargeBoxId);
    void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode);
    void ocppTransactionStarted(String chargeBoxId, int transactionId, int connectorId);
    void ocppTransactionEnded(String chargeBoxId, int transactionId);
}

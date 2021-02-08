package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.service.OcppNotificationService;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;

import java.util.Optional;

@Slf4j
public class TestNotificationService implements OcppNotificationService {

    @Override
    public void ocppStationBooted(String chargeBoxId, Optional<RegistrationStatus> status) {
        log.info("ocppStationBooted notification");
    }

    @Override
    public void ocppStationWebSocketConnected(String chargeBoxId) {
        log.info("ocppStationWebSocketConnected notification");
    }

    @Override
    public void ocppStationWebSocketDisconnected(String chargeBoxId) {
        log.info("ocppStationWebSocketDisconnected notification");

    }

    @Override
    public void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode) {
        log.info("ocppStationStatusFailure notification");
    }

    @Override
    public void ocppTransactionStarted(int transactionId, InsertTransactionParams params) {
        log.info("ocppTransactionStarted notification");
    }

    @Override
    public void ocppTransactionEnded(UpdateTransactionParams params) {
        log.info("ocppTransactionEnded notification");
    }
}

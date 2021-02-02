package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class TestNotificationService implements NotificationService {

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
    public void ocppTransactionStarted(String chargeBoxId, int transactionId, int connectorId) {
        log.info("ocppTransactionStarted notification");
    }

    @Override
    public void ocppTransactionEnded(String chargeBoxId, int transactionId) {
        log.info("ocppTransactionEnded notification");
    }
}

package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.rwth.idsg.steve.NotificationFeature.OcppStationBooted;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationStatusFailure;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationWebSocketConnected;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationWebSocketDisconnected;
import static de.rwth.idsg.steve.NotificationFeature.OcppTransactionStarted;
import static de.rwth.idsg.steve.NotificationFeature.OcppTransactionEnded;
import static java.lang.String.format;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private MailService mailService;

    public void ocppStationBooted(String chargeBoxId, Optional<RegistrationStatus> status) {
        if (isDisabled(OcppStationBooted)) {
            return;
        }

        String subject = format("Received boot notification from '%s'", chargeBoxId);
        String body;
        if (status.isPresent()) {
            body = format("Charging station '%s' is in database and has registration status '%s'.", chargeBoxId, status.get().value());
        } else {
            body = format("Charging station '%s' is NOT in database", chargeBoxId);
        }

        mailService.sendAsync(subject, addTimestamp(body));
    }

    public void ocppStationWebSocketConnected(String chargeBoxId) {
        if (isDisabled(OcppStationWebSocketConnected)) {
            return;
        }

        String subject = format("Connected to JSON charging station '%s'", chargeBoxId);

        mailService.sendAsync(subject, addTimestamp(""));
    }

    public void ocppStationWebSocketDisconnected(String chargeBoxId) {
        if (isDisabled(OcppStationWebSocketDisconnected)) {
            return;
        }

        String subject = format("Disconnected from JSON charging station '%s'", chargeBoxId);

        mailService.sendAsync(subject, addTimestamp(""));
    }

    public void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode) {
        if (isDisabled(OcppStationStatusFailure)) {
            return;
        }

        String subject = format("Connector '%s' of charging station '%s' is FAULTED", connectorId, chargeBoxId);
        String body = format("Status Error Code: '%s'", errorCode);

        mailService.sendAsync(subject, addTimestamp(body));
    }

    public void ocppTransactionStarted(String chargeBoxId, int transactionId, int connectorId) {
        if (isDisabled(OcppTransactionStarted)) {
            return;
        }

        String subject = format("Transaction '%s' has started on charging station '%s' on connector '%s'", transactionId, chargeBoxId, connectorId);

        mailService.sendAsync(subject, addTimestamp(""));
    }

    public void ocppTransactionEnded(String chargeBoxId, int transactionId) {
        if (isDisabled(OcppTransactionEnded)) {
            return;
        }

        String subject = format("Transaction '%s' has ended on charging station '%s'", transactionId, chargeBoxId);

        mailService.sendAsync(subject, addTimestamp(""));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private boolean isDisabled(NotificationFeature f) {
        MailSettings settings = mailService.getSettings();

        boolean isEnabled = settings.isEnabled()
                && settings.getEnabledFeatures().contains(f)
                && !settings.getRecipients().isEmpty();

        return !isEnabled;
    }

    private static String addTimestamp(String body) {
        String eventTs = "Timestamp of the event: " + DateTime.now().toString();
        String newLine = "\r\n\r\n";

        if (Strings.isNullOrEmpty(body)) {
            return eventTs;
        } else {
            return body + newLine + "--" + newLine + eventTs;
        }
    }
}
/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.service.notification.OccpStationBooted;
import de.rwth.idsg.steve.service.notification.OcppStationStatusFailure;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketDisconnected;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppStationStatusSuspendedEV;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static de.rwth.idsg.steve.NotificationFeature.OcppStationBooted;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationStatusFailure;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationWebSocketConnected;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationWebSocketDisconnected;
import static de.rwth.idsg.steve.NotificationFeature.OcppTransactionStarted;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationStatusSuspendedEV;
import static de.rwth.idsg.steve.NotificationFeature.OcppTransactionEnded;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import static java.lang.String.format;
import jooq.steve.db.tables.records.UserRecord;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.01.2016
 */
@Slf4j
@Service
public class NotificationService {

    @Autowired private MailService mailService;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private ScheduledExecutorService executorService;

    @EventListener
    public void ocppStationBooted(OccpStationBooted notification) {
        if (isDisabled(OcppStationBooted)) {
            return;
        }

        String subject = format("Received boot notification from '%s'", notification.getChargeBoxId());
        String body;
        if (notification.getStatus().isPresent()) {
            body = format("Charging station '%s' is in database and has registration status '%s'.", notification.getChargeBoxId(), notification.getStatus().get().value());
        } else {
            body = format("Charging station '%s' is NOT in database", notification.getChargeBoxId());
        }

        mailService.sendAsync(subject, addTimestamp(body));
    }

    @EventListener
    public void ocppStationWebSocketConnected(OcppStationWebSocketConnected notification) {
        if (isDisabled(OcppStationWebSocketConnected)) {
            return;
        }

        String subject = format("Connected to JSON charging station '%s'", notification.getChargeBoxId());

        mailService.sendAsync(subject, addTimestamp(""));
    }

    @EventListener
    public void ocppStationWebSocketDisconnected(OcppStationWebSocketDisconnected notification) {
        if (isDisabled(OcppStationWebSocketDisconnected)) {
            return;
        }

        String subject = format("Disconnected from JSON charging station '%s'", notification.getChargeBoxId());

        mailService.sendAsync(subject, addTimestamp(""));
    }

    @EventListener
    public void ocppStationStatusFailure(OcppStationStatusFailure notification) {
        if (isDisabled(OcppStationStatusFailure)) {
            return;
        }

        String subject = format("Connector '%s' of charging station '%s' is FAULTED",
                notification.getConnectorId(),
                notification.getChargeBoxId()
        );
        String body = format("Status Error Code: '%s'", notification.getErrorCode());

        mailService.sendAsync(subject, addTimestamp(body));
    }

    @EventListener
    public void ocppTransactionStarted(OcppTransactionStarted notification) {
        if (isDisabled(OcppTransactionStarted)) {
            return;
        }

        String subject = format("Transaction '%s' has started on charging station '%s' on connector '%s'",
                notification.getTransactionId(),
                notification.getParams().getChargeBoxId(),
                notification.getParams().getConnectorId()
        );

        mailService.sendAsync(subject, addTimestamp(createContent(notification.getParams())));
    }

    @EventListener
    public void ocppStationStatusSuspendedEV(OcppStationStatusSuspendedEV notification) {
         executorService.execute(() -> {
            try {
                notificationActionSuspendedEV(notification);
            } catch (Exception e) {
                log.error("Failed to execute the notification of SuspendedEV", e);
            }
        });
    }

    private void notificationActionSuspendedEV(OcppStationStatusSuspendedEV notification) {
        Integer connectorPk = ocppServerRepository.getConnectorPk(notification.getChargeBoxId(),
                notification.getConnectorId()
        );
        String ocppTag = transactionRepository.getOcppTagOfActiveTransaction(connectorPk);

        String subject = format("EV stopped charging at charging station %s, Connector %d",
                    notification.getChargeBoxId(),
                    notification.getConnectorId()
        );
        if (ocppTag != null) {
            String eMailAddress = null;
            UserRecord userRecord = new UserRecord();
            try {
                userRecord = userRepository.getDetails(ocppTag).getUserRecord();
                eMailAddress = userRecord.getEMail();
            } catch (Exception e) {
                log.error("Failed to send email (SuspendedEV). User not found! " + e.getMessage());
            }
            // send email if user with eMail address found
            if (!Strings.isNullOrEmpty(eMailAddress)) {
                String bodyUserMail =
                        format("User: %s %s \n\n Connector %d of charging station %s notifies Suspended_EV",
                                userRecord.getFirstName(),
                                userRecord.getLastName(),
                                notification.getConnectorId(),
                                notification.getChargeBoxId()
                        );

                mailService.sendAsync(subject, addTimestamp(bodyUserMail), eMailAddress);
            }
        }

        /* mail defined in settings */
        if (isDisabled(OcppStationStatusSuspendedEV)) {
            return;
        }
        String body = format("Connector %d of charging station %s notifies Suspended_EV",
                notification.getConnectorId(),
                notification.getChargeBoxId()
        );
        mailService.sendAsync(subject, addTimestamp(body));
    }

    @EventListener
    public void ocppTransactionEnded(OcppTransactionEnded notification) {
             executorService.execute(() -> {
            try {
                notificationActionTransactionEnded(notification);
            } catch (Exception e) {
                log.error("Failed to execute the notification of SuspendedEV", e);
            }
        });
    }

    private void notificationActionTransactionEnded(OcppTransactionEnded notification) {
        String eMailAddress = null;
        UserRecord userRecord = new UserRecord();

        Transaction transActParams = transactionRepository.getTransaction(notification.getParams().getTransactionId());

        try {
            userRecord = userRepository.getDetails(transActParams.getOcppIdTag()).getUserRecord();
            eMailAddress = userRecord.getEMail();
        } catch (Exception e) {
            log.error("Failed to send email (TransactionStop). User not found! " + e.getMessage());
        }

        // mail to user
        if (!Strings.isNullOrEmpty(eMailAddress)) {
            String subjectUserMail = format("Transaction '%s' has ended on charging station '%s'",
                    transActParams.getId(),
                    transActParams.getChargeBoxId()
            );

            // if the Transactionstop is received within the first Minute don't send an E-Mail
            if (transActParams.getStopTimestamp().isAfter(transActParams.getStartTimestamp().plusMinutes(1))) {
                mailService.sendAsync(subjectUserMail,
                        addTimestamp(createContent(transActParams, userRecord)),
                        eMailAddress
                );
            }
        }

        /* mail defined in settings */
        if (isDisabled(OcppTransactionEnded)) {
            return;
        }

        String subject = format("Transaction '%s' has ended on charging station '%s'",
                notification.getParams().getTransactionId(),
                notification.getParams().getChargeBoxId()
        );

        mailService.sendAsync(subject, addTimestamp(createContent(notification.getParams())));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String createContent(InsertTransactionParams params) {
        StringBuilder sb = new StringBuilder("Details:").append(System.lineSeparator())
            .append("- chargeBoxId: ").append(params.getChargeBoxId()).append(System.lineSeparator())
            .append("- connectorId: ").append(params.getConnectorId()).append(System.lineSeparator())
            .append("- idTag: ").append(params.getIdTag()).append(System.lineSeparator())
            .append("- startTimestamp: ").append(params.getStartTimestamp()).append(System.lineSeparator())
            .append("- startMeterValue: ").append(params.getStartMeterValue());

        if (params.isSetReservationId()) {
            sb.append(System.lineSeparator()).append("- reservationId: ").append(params.getReservationId());
        }

        return sb.toString();
    }

    private static String createContent(UpdateTransactionParams params) {
        return new StringBuilder("Details:").append(System.lineSeparator())
            .append("- chargeBoxId: ").append(params.getChargeBoxId()).append(System.lineSeparator())
            .append("- transactionId: ").append(params.getTransactionId()).append(System.lineSeparator())
            .append("- stopTimestamp: ").append(params.getStopTimestamp()).append(System.lineSeparator())
            .append("- stopMeterValue: ").append(params.getStopMeterValue()).append(System.lineSeparator())
            .append("- stopReason: ").append(params.getStopReason())
            .toString();
    }

    private static String createContent(Transaction params, UserRecord userRecord) {
        Double meterValueDiff;
        Integer meterValueStop;
        Integer meterValueStart;
        String strMeterValueDiff = "-";
        try {
            meterValueStop = Integer.valueOf(params.getStopValue());
            meterValueStart = Integer.valueOf(params.getStartValue());
            meterValueDiff = (meterValueStop - meterValueStart) / 1000.0; // --> kWh
            strMeterValueDiff = meterValueDiff.toString() + " kWh";
        } catch (NumberFormatException e) {
            log.error("Failed to calculate charged energy! ", e);
        }

        return new StringBuilder("User: ")
            .append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName())
            .append(System.lineSeparator())
            .append(System.lineSeparator())
            .append("Details:").append(System.lineSeparator())
            .append("- chargeBoxId: ").append(params.getChargeBoxId()).append(System.lineSeparator())
            .append("- connectorId: ").append(params.getConnectorId()).append(System.lineSeparator())
            .append("- transactionId: ").append(params.getId()).append(System.lineSeparator())
            .append("- startTimestamp (UTC): ").append(params.getStartTimestamp()).append(System.lineSeparator())
            .append("- startMeterValue: ").append(params.getStartValue()).append(System.lineSeparator())
            .append("- stopTimestamp (UTC): ").append(params.getStopTimestamp()).append(System.lineSeparator())
            .append("- stopMeterValue: ").append(params.getStopValue()).append(System.lineSeparator())
            .append("- stopReason: ").append(params.getStopReason()).append(System.lineSeparator())
            .append("- charged energy: ").append(strMeterValueDiff).append(System.lineSeparator())
            .toString();
    }

    private boolean isDisabled(NotificationFeature f) {
        MailSettings settings = mailService.getSettings();

        boolean isEnabled = settings.isEnabled()
                && settings.getEnabledFeatures().contains(f)
                && !settings.getRecipients().isEmpty();

        return !isEnabled;
    }

    private static String addTimestamp(String body) {
        String eventTs = "Timestamp of the event: " + DateTime.now();
        String newLine = System.lineSeparator() + System.lineSeparator();

        if (Strings.isNullOrEmpty(body)) {
            return eventTs;
        } else {
            return body + newLine + "--" + newLine + eventTs;
        }
    }

}

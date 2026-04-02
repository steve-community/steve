/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.notification.OcppStationStatusFailure;
import de.rwth.idsg.steve.service.notification.OcppStationStatusSuspendedEV;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import de.rwth.idsg.steve.utils.TransactionStopServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceForUser {

    private final MailService mailService;
    private final TransactionService transactionService;
    private final UserService userService;

    @Async
    @EventListener
    public void ocppStationStatusFailure(OcppStationStatusFailure event) {
        log.debug("Processing: {}", event);

        var transaction = transactionService.getLatestActiveTransaction(event.getChargeBoxId(), event.getConnectorId());
        if (transaction == null) {
            return;
        }

        var user = userService.getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppStationStatusFailure);
        if (user == null) {
            return;
        }

        String subject = "Connector '%s' of charging station '%s' is FAULTED".formatted(
            event.getConnectorId(),
            event.getChargeBoxId()
        );

        // send email if user with eMail address found
        String bodyUserMail =
            "User: %s \n\n Connector %d of charging station %s notifies FAULTED! \n\n Error code: %s".formatted(
                user.getName(),
                event.getConnectorId(),
                event.getChargeBoxId(),
                event.getErrorCode()
            );

        mailService.send(subject, addTimestamp(bodyUserMail), List.of(user.getEmail()));
    }

    @Async
    @EventListener
    public void ocppTransactionStarted(OcppTransactionStarted event) {
        log.debug("Processing: {}", event);

        var user = userService.getUserForMail(event.getParams().getIdTag(), NotificationFeature.OcppTransactionStarted);
        if (user == null) {
            return;
        }

        String subject = "Transaction '%s' has started on charging station '%s' on connector '%s'".formatted(
            event.getTransactionId(),
            event.getParams().getChargeBoxId(),
            event.getParams().getConnectorId()
        );

        // send email if user with eMail address found
        String bodyUserMail =
            "User: %s started transaction '%d' on connector '%s' of charging station '%s'".formatted(
                user.getName(),
                event.getTransactionId(),
                event.getParams().getConnectorId(),
                event.getParams().getChargeBoxId()
            );

        mailService.send(subject, addTimestamp(bodyUserMail), List.of(user.getEmail()));
    }

    @Async
    @EventListener
    public void ocppStationStatusSuspendedEV(OcppStationStatusSuspendedEV event) {
        log.debug("Processing: {}", event);

        var transaction = transactionService.getLatestActiveTransaction(event.getChargeBoxId(), event.getConnectorId());
        if (transaction == null) {
            return;
        }

        var user = userService.getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppStationStatusSuspendedEV);
        if (user == null) {
            return;
        }

        String subject = "EV stopped charging at charging station %s, Connector %d".formatted(
            event.getChargeBoxId(),
            event.getConnectorId()
        );

        // send email if user with eMail address found
        String bodyUserMail =
            "User: %s \n\n Connector %d of charging station %s notifies Suspended_EV".formatted(
                user.getName(),
                event.getConnectorId(),
                event.getChargeBoxId()
            );

        mailService.send(subject, addTimestamp(bodyUserMail), List.of(user.getEmail()));
    }

    @Async
    @EventListener
    public void ocppTransactionEnded(OcppTransactionEnded event) {
        log.debug("Processing: {}", event);

        var transaction = transactionService.getTransaction(event.getParams().getTransactionId());
        if (transaction == null) {
            return;
        }

        var user = userService.getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppTransactionEnded);
        if (user == null) {
            return;
        }

        String subject = "Transaction '%s' has ended on charging station '%s'".formatted(
            event.getParams().getTransactionId(),
            event.getParams().getChargeBoxId()
        );

        mailService.send(subject, addTimestamp(createContent(transaction, user)), List.of(user.getEmail()));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String createContent(Transaction params, User.Overview user) {
        Double consumption = TransactionStopServiceHelper.calculateEnergyConsumptionInKWh(params);
        String strMeterValueDiff = (consumption == null) ? "- kWh" : consumption + " kWh";

        return new StringBuilder("User: ")
            .append(user.getName())
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
            .append("- charged energy: ").append(strMeterValueDiff)
            .toString();
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

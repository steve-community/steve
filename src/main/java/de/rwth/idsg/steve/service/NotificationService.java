/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.01.2016
 */
@Slf4j
@Service
public class NotificationService {

    @Autowired private MailService mailService;

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

    public void ocppTransactionStarted(int transactionId, InsertTransactionParams params) {
        if (isDisabled(OcppTransactionStarted)) {
            return;
        }

        String subject = format("Transaction '%s' has started on charging station '%s' on connector '%s'", transactionId, params.getChargeBoxId(), params.getConnectorId());

        mailService.sendAsync(subject, addTimestamp(createContent(params)));
    }

    public void ocppTransactionEnded(UpdateTransactionParams params) {
       if (isDisabled(OcppTransactionEnded)) {
            return;
        }

        String subject = format("Transaction '%s' has ended on charging station '%s'", params.getTransactionId(), params.getChargeBoxId());

        mailService.sendAsync(subject, addTimestamp(createContent(params)));
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

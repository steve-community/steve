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
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.notification.OcppStationStatusFailure;
import de.rwth.idsg.steve.service.notification.OcppStationStatusSuspendedEV;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

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
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void ocppStationStatusFailure(OcppStationStatusFailure event) {
        String subject = format("Connector '%s' of charging station '%s' is FAULTED",
                event.getConnectorId(),
                event.getChargeBoxId()
        );

        var transaction = transactionService.getActiveTransaction(event.getChargeBoxId(), event.getConnectorId());
        if (transaction == null) {
            return;
        }

        var user = getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppStationStatusFailure);
        if (user == null) {
            return;
        }

        // send email if user with eMail address found
        String bodyUserMail =
                format("User: %s \n\n Connector %d of charging station %s notifies FAULTED! \n\n Error code: %s",
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
        String subject = format("Transaction '%s' has started on charging station '%s' on connector '%s'",
                event.getTransactionId(),
                event.getParams().getChargeBoxId(),
                event.getParams().getConnectorId()
        );

        var user = getUserForMail(event.getParams().getIdTag(), NotificationFeature.OcppTransactionStarted);
        if (user == null) {
            return;
        }

        // send email if user with eMail address found
        String bodyUserMail =
                format("User: %s started transaction '%d' on connector '%s' of charging station '%s'",
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
        String subject = format("EV stopped charging at charging station %s, Connector %d",
                    event.getChargeBoxId(),
                    event.getConnectorId()
        );

        var transaction = transactionService.getActiveTransaction(event.getChargeBoxId(), event.getConnectorId());
        if (transaction == null) {
            return;
        }

        // No mail directly after the start of the transaction,
        if (!event.getTimestamp().isAfter(transaction.getStartTimestamp().plusMinutes(1))) {
            return;
        }

        var user = getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppStationStatusSuspendedEV);
        if (user == null) {
            return;
        }

        // send email if user with eMail address found
        String bodyUserMail =
                format("User: %s \n\n Connector %d of charging station %s notifies Suspended_EV",
                        user.getName(),
                        event.getConnectorId(),
                        event.getChargeBoxId()
                );
        mailService.send(subject, addTimestamp(bodyUserMail), List.of(user.getEmail()));
    }

    @Async
    @EventListener
    public void ocppTransactionEnded(OcppTransactionEnded event) {
        String subject = format("Transaction '%s' has ended on charging station '%s'",
                event.getParams().getTransactionId(),
                event.getParams().getChargeBoxId()
        );

        var transaction = transactionService.getTransaction(event.getParams().getTransactionId());

        // if the TransactionStop is received within the first Minute don't send an E-Mail
        if (!transaction.getStopTimestamp().isAfter(transaction.getStartTimestamp().plusMinutes(1))) {
            return;
        }

        var user = getUserForMail(transaction.getOcppIdTag(), NotificationFeature.OcppTransactionEnded);
        if (user == null) {
            return;
        }

        mailService.send(subject, addTimestamp(createContent(transaction, user)), List.of(user.getEmail()));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private User.Overview getUserForMail(String ocppIdTag, NotificationFeature feature) {
        UserQueryForm form = new UserQueryForm();
        form.setOcppIdTag(ocppIdTag);

        List<User.Overview> overview = userRepository.getOverview(form);
        if (overview.isEmpty()) {
            return null;
        } else if (overview.size() > 1) {
            // should not happen
            log.warn("Multiple users found for OcppTag {}", ocppIdTag);
            return null;
        }

        var user = overview.get(0);
        if (!hasOcppTag(user, ocppIdTag)) {
            return null;
        }

        String eMailAddress = user.getEmail();
        if (Strings.isNullOrEmpty(eMailAddress)) {
            return null;
        }

        if (!user.getEnabledFeatures().contains(feature)) {
            return null;
        }

        return user;
    }

    /**
     * We check this here again, because userRepository.getOverview(..) also returns partial match OcppTags.
     */
    private static boolean hasOcppTag(User.Overview user, String ocppIdTag) {
        for (User.OcppTagEntry ocppTagEntry : user.getOcppTagEntries()) {
            if (ocppTagEntry.getIdTag().equals(ocppIdTag)) {
                return true;
            }
        }
        return false;
    }

    private static String createContent(Transaction params, User.Overview user) {
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
            .append("- charged energy: ").append(strMeterValueDiff).append(System.lineSeparator())
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

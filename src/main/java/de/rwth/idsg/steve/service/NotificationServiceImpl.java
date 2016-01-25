package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.rwth.idsg.steve.NotificationFeature.OcppStationBooted;
import static de.rwth.idsg.steve.NotificationFeature.OcppStationStatusFailure;
import static java.lang.String.format;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired private MailService mailService;

    @Override
    public void ocppStationBooted(String chargeBoxId, boolean isRegistered) {
        if (isDisabled(OcppStationBooted)) {
            return;
        }

        String subject = format("Received boot notification from '%s'", chargeBoxId);
        String body = isRegistered ? "" : format("Charging station '%s' is NOT registered.", chargeBoxId);

        mailService.sendAsync(subject, addTimestamp(body));
    }

    @Override
    public void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode) {
        if (isDisabled(OcppStationStatusFailure)) {
            return;
        }

        String subject = format("Connector '%s' of charging station '%s' is FAULTED", connectorId, chargeBoxId);
        String body = format("Status Error Code: '%s'", errorCode);

        mailService.sendAsync(subject, addTimestamp(body));
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

    private String addTimestamp(String body) {
        String eventTs = "Timestamp of the event: " + DateTime.now().toString();
        String newLine = "\r\n\r\n";

        if (Strings.isNullOrEmpty(body)) {
            return eventTs;
        } else {
            return body + newLine + "--" + newLine + eventTs;
        }
    }

}

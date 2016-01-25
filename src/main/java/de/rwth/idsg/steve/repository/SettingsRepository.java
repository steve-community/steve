package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.MailSettings;
import de.rwth.idsg.steve.web.dto.SettingsForm;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.11.2015
 */
public interface SettingsRepository {
    SettingsForm getForm();
    MailSettings getMailSettings();
    int getHeartbeatIntervalInSeconds();
    int getHoursToExpire();
    void update(SettingsForm settingsForm);
}

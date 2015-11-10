package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.Settings;
import de.rwth.idsg.steve.web.dto.SettingsForm;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.11.2015
 */
public interface SettingsRepository {
    Settings get();
    int getHeartbeatIntervalInSeconds();
    int getHoursToExpire();
    void update(SettingsForm settingsForm);
}

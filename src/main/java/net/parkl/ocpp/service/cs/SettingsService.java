package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.MailSettings;
import de.rwth.idsg.steve.web.dto.SettingsForm;

public interface SettingsService {

	int getHeartbeatIntervalInSeconds();

	int getHoursToExpire();

	MailSettings getMailSettings();

	SettingsForm getForm();

	void update(SettingsForm settingsForm);

	boolean isSettingsExisting();

	void saveDefaultSettings();

}

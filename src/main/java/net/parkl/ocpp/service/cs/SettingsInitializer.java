package net.parkl.ocpp.service.cs;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsInitializer.class);
	
	@Autowired
	private SettingsService settingService;
	@PostConstruct
	public void init() {
		if (!settingService.isSettingsExisting()) {
			LOGGER.info("Initializing default settings...");
			settingService.saveDefaultSettings();
		}
	}
}

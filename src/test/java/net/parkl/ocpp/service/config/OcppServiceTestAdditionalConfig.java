package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcppServiceTestAdditionalConfig {

	
	@Bean
	public OcppTagService ocppTagService() {
		return new OcppTagServiceImpl();
	}
	
	@Bean
	public CentralSystemService16_Service centralSystemService16_Service() {
		return new CentralSystemService16_Service();
	}
	
	@Bean
	public MailService mailService() {
		return new MailService();
	}
	@Bean
	public NotificationService notificationService() {
		return new NotificationServiceImpl();
	}
}

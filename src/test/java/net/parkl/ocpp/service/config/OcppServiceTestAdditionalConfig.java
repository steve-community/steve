package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.service.NotificationService;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.service.OcppTagServiceImpl;
import net.parkl.ocpp.service.TestNotificationService;
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
    public NotificationService notificationService() {
        return new TestNotificationService();
    }
}

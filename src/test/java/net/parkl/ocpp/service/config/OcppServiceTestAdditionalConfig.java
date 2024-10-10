package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.service.*;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.service.cs.SettingsService;
import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcppServiceTestAdditionalConfig {


    @Autowired
    private SettingsService settingsService;
    @Autowired
    private TransactionService transactionService;








    @Bean
    public CentralSystemService16_Service centralSystemService16_Service() {
        return new CentralSystemService16_Service();
    }
}

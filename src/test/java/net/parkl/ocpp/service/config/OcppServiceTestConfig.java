package net.parkl.ocpp.service.config;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.service.AuthTagService;
import de.rwth.idsg.steve.service.AuthTagServiceRemote;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.service.OcppTagServiceImpl;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;


@Configuration
@ComponentScan(basePackages = {"net.parkl.ocpp","de.rwth.idsg.steve.repository","de.rwth.idsg.steve.ocpp",
		"de.rwth.idsg.steve.service","de.rwth.idsg.steve.ocpp.ws.cluster"})
@Import({ JpaTestConfig.class, OcppServiceTestAdditionalConfig.class})
public class OcppServiceTestConfig {
	
	@Bean("taskExecutor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor e=new ThreadPoolTaskExecutor();
		e.setCorePoolSize(5);
		e.setMaxPoolSize(25);
		e.setQueueCapacity(10);
		return e;
	}
	
	@Bean
	public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		return new ScheduledThreadPoolExecutor(10);
	}

	@Bean
	public SteveConfiguration steveConfiguration() {
		return new SteveConfiguration();
	}



}

package net.parkl.ocpp.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;


@Configuration
@ComponentScan(basePackages = {"net.parkl.ocpp","de.rwth.idsg.steve.repository"})
@Import({ JpaTestConfig.class, OcppServiceTestAdditionalConfig.class})
public class OcppServiceTestConfig {
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
       PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
       configurer.setLocations(new ClassPathResource("/config/test-ocpp.properties"));
       return configurer;
    }
	
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
	
	
}

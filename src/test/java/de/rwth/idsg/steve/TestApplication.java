package net.parkl.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

import java.util.Map;

@SpringBootApplication
@ComponentScan(basePackages = {"net.parkl.stevep.ocpp.service","de.rwth.idsg.steve"})
@EntityScan("net.parkl.stevep.ocpp.entities")
@EnableJpaRepositories("net.parkl.stevep.ocpp.repositories")
public class TestApplication  {

    public TestApplication() {
        super();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }


    public static void main(String[] args) {

        SpringApplication.run(TestApplication.class, args);
    }

} 
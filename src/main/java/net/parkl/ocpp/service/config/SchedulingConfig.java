package net.parkl.ocpp.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SchedulingConfig {
    @Getter
    @Value("${ocpp.scheduling.http.enabled:false}")
    private boolean httpSchedulingEnabled;
}

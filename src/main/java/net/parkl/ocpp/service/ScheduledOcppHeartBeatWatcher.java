package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.service.config.SchedulingConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledOcppHeartBeatWatcher {

    private final OcppHeartBeatWatcher heartBeatWatcher;
    private final SchedulingConfig schedulingConfig;


    @Scheduled(fixedRate = 300000)
    public void watch() {
        if (!schedulingConfig.isHttpSchedulingEnabled()) {
            heartBeatWatcher.watch();
        }
    }
}

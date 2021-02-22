package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledOcppHeartBeatWatcher {

    private final OcppHeartBeatWatcher heartBeatWatcher;

    public ScheduledOcppHeartBeatWatcher(OcppHeartBeatWatcher heartBeatWatcher) {
        this.heartBeatWatcher = heartBeatWatcher;
    }

    @Scheduled(fixedRate = 300000)
    public void watch() {
        heartBeatWatcher.watch();
    }
}

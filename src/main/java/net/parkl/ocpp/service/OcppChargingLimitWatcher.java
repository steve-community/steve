package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OcppChargingLimitWatcher {

    private final OcppProxyService proxyService;
    private final OcppMiddleware facade;
    private final TaskExecutor taskExecutor;

    public OcppChargingLimitWatcher(OcppProxyService proxyService, OcppMiddleware facade, TaskExecutor taskExecutor) {
        this.proxyService = proxyService;
        this.facade = facade;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedRate = 10000)
    public void checkChargingLimit() {
        log.debug("Checking charging limit...");

        List<OcppChargingProcess> kwhLimitProcesses = proxyService.findOpenChargingProcessesWithLimitKwh();
        for (OcppChargingProcess cp : kwhLimitProcesses) {
            if (cp.getTransaction() != null) {
                log.info("Checking charging process with kwh limit: {}...", cp.getOcppChargingProcessId());
                PowerValue pw = facade.getPowerValue(cp.getTransaction());
                float kWh = OcppConsumptionHelper.getKwhValue(pw.getValue(), pw.getUnit());
                if (kWh >= cp.getLimitKwh()) {
                    log.info("Limit {} exceeded ({}) for charging process, stopping: {}...", cp.getLimitKwh(), kWh, cp.getOcppChargingProcessId());

                    taskExecutor.execute(() -> facade.stopChargingWithLimit(cp.getOcppChargingProcessId(), cp.getLimitKwh()));
                }
            }
        }

        List<OcppChargingProcess> minuteLimitProcesses = proxyService.findOpenChargingProcessesWithLimitMinute();
        for (OcppChargingProcess cp : minuteLimitProcesses) {
            if (cp.getTransaction() != null) {
                log.info("Checking charging process with minute limit: {}...", cp.getOcppChargingProcessId());
                int duration = Duration.between(cp.getStartDate().toInstant(), new Date().toInstant()).toMinutesPart();
                if (duration >= cp.getLimitMinute()) {
                    log.info("Limit {} exceeded ({}) for charging process, stopping: {}...", cp.getLimitMinute(), duration, cp.getOcppChargingProcessId());

                    PowerValue pw = facade.getPowerValue(cp.getTransaction());
                    float kWh = OcppConsumptionHelper.getKwhValue(pw.getValue(), pw.getUnit());

                    taskExecutor.execute(() -> facade.stopChargingWithLimit(cp.getOcppChargingProcessId(), kWh));
                }
            }
        }
    }
}

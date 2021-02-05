package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.System.currentTimeMillis;

@Component
@Slf4j
public class OcppStartTimeoutManager {
    private final AdvancedChargeBoxConfiguration config;
    private final ChargingProcessService proxyService;
    private final OcppMiddleware facade;

    public OcppStartTimeoutManager(AdvancedChargeBoxConfiguration config,
                                   ChargingProcessService proxyService,
                                   OcppMiddleware facade) {
        this.config = config;
        this.proxyService = proxyService;
        this.facade = facade;
    }

    @Scheduled(fixedRate = 10000)
    public void checkForStartTimeout() {
        if (!config.isStartTimeoutEnabledForAny() && !config.isPreparingTimeoutEnabledForAny()) {
            return;
        }
        log.debug("Checking start timeout...");

        List<OcppChargingProcess> processes = proxyService.findOpenChargingProcessesWithoutTransaction();
        for (OcppChargingProcess cp : processes) {
            if (config.isStartTimeoutEnabled(cp.getConnector().getChargeBoxId())) {
                log.info("Checking for timeout of process: {}...", cp.getOcppChargingProcessId());
                try {
                    checkForProcessStartTimeout(cp);
                } catch (Exception ex) {
                    log.error("Failed to process start timeout: " + cp.getOcppChargingProcessId(), ex);
                }
            } else if (config.isPreparingTimeoutEnabled(cp.getConnector().getChargeBoxId())) {
                log.info("Checking for preparing timeout of process: {}...", cp.getOcppChargingProcessId());
                try {
                    checkForProcessPreparingTimeout(cp);
                } catch (Exception ex) {
                    log.error("Failed to process start timeout: " + cp.getOcppChargingProcessId(), ex);
                }
            }
        }
    }

    private void checkForProcessStartTimeout(OcppChargingProcess chargingProcess) throws Exception {
        if (chargingProcess.getStartDate().getTime()
                + config.getStartTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()) * 1000L < currentTimeMillis()) {
            log.info("Charging process start timeout: {}", chargingProcess.getOcppChargingProcessId());
            log.info("Charging process stopped on timeout, connector reset: {}-{}",
                     chargingProcess.getConnector().getChargeBoxId(), chargingProcess.getConnector().getConnectorId());
            changeAvailability(chargingProcess);
        }
    }

    private void checkForProcessPreparingTimeout(OcppChargingProcess chargingProcess) {
        if (chargingProcess.getStartDate().getTime()
                + config.getPreparingTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()) * 1000L < currentTimeMillis()) {
            log.info("Charging process preparing timeout: {}", chargingProcess.getOcppChargingProcessId());
            log.info("Charging process stopped on timeout, connector reset: {}-{}",
                     chargingProcess.getConnector().getChargeBoxId(), chargingProcess.getConnector().getConnectorId());
            facade.stopChargingWithPreparingTimeout(chargingProcess.getOcppChargingProcessId());
        }
    }

    private void changeAvailability(OcppChargingProcess chargingProcess) throws InterruptedException {
        facade.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                                  String.valueOf(chargingProcess.getConnector().getConnectorId()),
                                  false);
        Thread.sleep(1000);
        facade.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                                  String.valueOf(chargingProcess.getConnector().getConnectorId()),
                                  true);
    }
}

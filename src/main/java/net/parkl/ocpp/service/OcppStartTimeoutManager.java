package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.System.currentTimeMillis;

@Component
public class OcppStartTimeoutManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppStartTimeoutManager.class);

    private final AdvancedChargeBoxConfiguration config;
    private final ChargingProcessService proxyService;

    private final OcppMiddleware facade;

    public OcppStartTimeoutManager(AdvancedChargeBoxConfiguration config, ChargingProcessService proxyService, OcppMiddleware facade) {
        this.config = config;
        this.proxyService = proxyService;
        this.facade = facade;
    }

    @Scheduled(fixedRate = 10000)
    public void checkForStartTimeout() {

        if (!config.isStartTimeoutEnabledForAny() && !config.isPreparingTimeoutEnabledForAny()) {
            return;
        }
        LOGGER.debug("Checking start timeout...");

        List<OcppChargingProcess> processes = proxyService.findOpenChargingProcessesWithoutTransaction();
        for (OcppChargingProcess cp : processes) {
            if (config.isStartTimeoutEnabled(cp.getConnector().getChargeBoxId())) {
                LOGGER.info("Checking for timeout of process: {}...", cp.getOcppChargingProcessId());
                try {
                    checkForProcessStartTimeout(cp);
                } catch (Exception ex) {
                    LOGGER.error("Failed to process start timeout: " + cp.getOcppChargingProcessId(), ex);
                }
            } else if (config.isPreparingTimeoutEnabled(cp.getConnector().getChargeBoxId())) {
                LOGGER.info("Checking for preparing timeout of process: {}...", cp.getOcppChargingProcessId());
                try {
                    checkForProcessPreparingTimeout(cp);
                } catch (Exception ex) {
                    LOGGER.error("Failed to process start timeout: " + cp.getOcppChargingProcessId(), ex);
                }
            }
        }
    }

    private void checkForProcessStartTimeout(OcppChargingProcess chargingProcess) throws Exception {
        if (chargingProcess.getStartDate().getTime()
                + config.getStartTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()) * 1000L < currentTimeMillis()) {
            LOGGER.info("Charging process start timeout: {}", chargingProcess.getOcppChargingProcessId());
            LOGGER.info("Charging process stopped on timeout, connector reset: {}-{}",
                    chargingProcess.getConnector().getChargeBoxId(), chargingProcess.getConnector().getConnectorId());
            changerAvailability(chargingProcess);
        }
    }

    private void checkForProcessPreparingTimeout(OcppChargingProcess chargingProcess) {
        if (chargingProcess.getStartDate().getTime()
                + config.getPreparingTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()) * 1000L < currentTimeMillis()) {
            LOGGER.info("Charging process preparing timeout: {}", chargingProcess.getOcppChargingProcessId());
            LOGGER.info("Charging process stopped on timeout, connector reset: {}-{}",
                    chargingProcess.getConnector().getChargeBoxId(), chargingProcess.getConnector().getConnectorId());
            facade.stopChargingWithPreparingTimeout(chargingProcess.getOcppChargingProcessId());
        }
    }

    private void changerAvailability(OcppChargingProcess chargingProcess) throws InterruptedException {
        facade.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                String.valueOf(chargingProcess.getConnector().getConnectorId()),
                false);
        Thread.sleep(1000);
        facade.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                String.valueOf(chargingProcess.getConnector().getConnectorId()),
                true);
    }


}

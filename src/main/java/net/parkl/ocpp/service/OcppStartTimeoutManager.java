/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.parkl.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppChargePointMiddleware;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.System.currentTimeMillis;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppStartTimeoutManager {
    private final AdvancedChargeBoxConfiguration config;
    private final ChargingProcessService proxyService;
    private final OcppChargingMiddleware chargingMiddleware;
    private final OcppChargePointMiddleware chargePointMiddleware;



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
            chargingMiddleware.stopChargingWithPreparingTimeout(chargingProcess.getOcppChargingProcessId());
        }
    }

    private void changeAvailability(OcppChargingProcess chargingProcess) throws InterruptedException {
        chargePointMiddleware.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                                  String.valueOf(chargingProcess.getConnector().getConnectorId()),
                                  false);
        Thread.sleep(1000);
        chargePointMiddleware.changeAvailability(chargingProcess.getConnector().getChargeBoxId(),
                                  String.valueOf(chargingProcess.getConnector().getConnectorId()),
                                  true);
    }
}

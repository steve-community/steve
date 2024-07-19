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

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;

@Service
@Slf4j
public class ESPNotificationService {
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor executor;

    @Autowired
    private OcppChargingMiddleware chargingMiddleware;

    public void notifyAboutChargingStopped(OcppChargingProcess chargingProcess) {
        executor.execute(() -> {
            log.info("Notifying ESP about stop transaction from charger: {}...",
                    chargingProcess.getTransactionStart().getTransactionPk());
            chargingMiddleware.stopChargingExternal(chargingProcess, REASON_VEHICLE_CHARGED);
        });
    }

    public void notifyAboutConsumptionUpdated(OcppChargingProcess chargingProcess, String startValue, String stopValue) {
        executor.execute(() -> {
            log.info("Notifying ESP about consumption of transaction: {}...",
                    chargingProcess.getTransactionStart().getTransactionPk());
            log.info("start: {}, stop: {}", startValue, stopValue);
            chargingMiddleware.updateConsumption(chargingProcess, startValue, stopValue);
        });
    }
}

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledOcppChargingLimitWatcher {

    private final OcppChargingLimitWatcher limitWatcher;

    public ScheduledOcppChargingLimitWatcher(OcppChargingLimitWatcher limitWatcher) {
        this.limitWatcher = limitWatcher;
    }

    @Scheduled(fixedRate = 10000)
    public void checkChargingLimit() {
        log.debug("Checking charging limit...");
        limitWatcher.checkKwhChargingLimit();
        limitWatcher.checkMinuteChargingLimit();
    }
}

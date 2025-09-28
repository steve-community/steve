/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.ocpp20.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Getter
@Configuration
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20Configuration {

    @Value("${ocpp.v20.enabled}")
    private boolean enabled;

    @Value("${ocpp.v20.beta.charge-box-ids:}")
    private String betaChargeBoxIdsString;

    private List<String> betaChargeBoxIds;

    @PostConstruct
    public void init() {
        if (betaChargeBoxIdsString != null && !betaChargeBoxIdsString.trim().isEmpty()) {
            betaChargeBoxIds = Arrays.asList(betaChargeBoxIdsString.split(","));
        } else {
            betaChargeBoxIds = Collections.emptyList();
        }

        log.info("OCPP 2.0 Configuration:");
        log.info("  OCPP 2.0 Enabled: {}", enabled);
        if (!betaChargeBoxIds.isEmpty()) {
            log.info("  Beta Charge Points: {}", String.join(", ", betaChargeBoxIds));
            log.info("  Only these charge points can use OCPP 2.0");
        } else {
            log.info("  Beta Mode: DISABLED - All charge points can use OCPP 2.0");
        }
    }

    public boolean isChargePointAllowed(String chargePointId) {
        if (betaChargeBoxIds.isEmpty()) {
            return true;
        }
        return betaChargeBoxIds.contains(chargePointId);
    }

    public boolean isBetaModeEnabled() {
        return !betaChargeBoxIds.isEmpty();
    }
}
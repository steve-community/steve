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
package net.parkl.ocpp.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * OCPP szerver külső konfigurációs komponens
 *
 * @author andor
 */
@Component
public class AdvancedChargeBoxConfiguration {
    @Autowired(required = false)
    private ChargeBoxConfiguration chargeBoxConfiguration;
    @Autowired(required = false)
    private IntegratedIdTagProvider idTagProvider;

    public boolean isStartTimeoutEnabled(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isStartTimeoutEnabled(chargeBoxId);
    }

    public int getStartTimeoutSecs(String chargeBoxId) {
        return chargeBoxConfiguration == null ? 0 : chargeBoxConfiguration.getStartTimeoutSecs(chargeBoxId);
    }

    public boolean isPreparingTimeoutEnabled(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isPreparingTimeoutEnabled(chargeBoxId);
    }

    public int getPreparingTimeoutSecs(String chargeBoxId) {
        return chargeBoxConfiguration == null ? 0 : chargeBoxConfiguration.getPreparingTimeoutSecs(chargeBoxId);
    }

    public boolean isTransactionPartialEnabled(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isTransactionPartialEnabled(chargeBoxId);
    }



    public boolean isStartTimeoutEnabledForAny() {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isStartTimeoutEnabledForAny();
    }

    public boolean isPreparingTimeoutEnabledForAny() {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isPreparingTimeoutEnabledForAny();
    }

    public boolean isUsingIntegratedTag(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isUsingIntegratedTag(chargeBoxId);
    }

    public List<String> getIntegrationIdTags() {
        return idTagProvider == null ? emptyList() : idTagProvider.integratedTags();
    }

    public boolean checkReservationId(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.checkReservationId(chargeBoxId);
    }

    public List<String> getChargeBoxesForAlert() {
        return chargeBoxConfiguration == null ? emptyList() : chargeBoxConfiguration.getChargeBoxesForAlert();
    }

    public boolean skipHeartBeatConfig(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.skipHeartBeatConfig(chargeBoxId);
    }

    public boolean isIdTagMax10Characters(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.isIdTagMax10Characters(chargeBoxId);
    }

    public float getWebSocketBufferMultiplier(String chargeBoxId, float defaultBufferMultiplier) {
        return chargeBoxConfiguration == null ? 1.0f : chargeBoxConfiguration.getWebSocketBufferMultiplier(chargeBoxId, defaultBufferMultiplier);
    }

    public boolean ignoreConnectorAvailableUntilStopTransaction(String chargeBoxId) {
        return chargeBoxConfiguration != null && chargeBoxConfiguration.ignoreConnectorAvailableUntilStopTransaction(chargeBoxId);
    }
}

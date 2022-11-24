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

import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static net.parkl.ocpp.service.config.AdvancedChargeBoxConfigKeys.*;

/**
 * OCPP szerver külső konfigurációs komponens
 *
 * @author andor
 */
@Component
public class AdvancedChargeBoxConfiguration {

    @Autowired
    private AdvancedChargeBoxConfigService chargeBoxConfigService;
    @Autowired(required = false)
    private IntegratedIdTagProvider idTagProvider;

    private boolean getConfigValueAsBool(String chargeBoxId, String key, boolean defaultValue) {
        AdvancedChargeBoxConfig config = chargeBoxConfigService.findByChargeBoxIdAndKey(chargeBoxId, key);
        if (config == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(config.getConfigValue());
    }

    private int getConfigValueAsInt(String chargeBoxId, String key, int defaultValue) {
        AdvancedChargeBoxConfig config = chargeBoxConfigService.findByChargeBoxIdAndKey(chargeBoxId, key);
        if (config == null) {
            return defaultValue;
        }
        return Integer.parseInt(config.getConfigValue());
    }

    public boolean isStartTimeoutEnabled(String chargeBoxId) {

        return getConfigValueAsBool(chargeBoxId, KEY_START_TIMEOUT_ENABLED, false);
    }

    public int getStartTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, KEY_START_TIMEOUT_SECS, 60);
    }

    public boolean isPreparingTimeoutEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_PREPARING_TIMEOUT_ENABLED, false);
    }

    public int getPreparingTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, KEY_PREPARING_TIMEOUT_SECS, 60);
    }

    public boolean isTransactionPartialEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_TRANSACTION_PARTIAL_ENABLED, false);
    }

    public boolean waitingForChargingProcessEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED, false);
    }

    public boolean isStartTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(KEY_START_TIMEOUT_ENABLED) > 0;
    }

    public boolean isPreparingTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(KEY_PREPARING_TIMEOUT_ENABLED) > 0;
    }

    public boolean isUsingIntegratedTag(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_USING_INTEGRATED_IDTAG, false);
    }

    public boolean isIdTagMax10Characters(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_IDTAG_MAX10, false);
    }

    public List<String> getIntegrationIdTags() {
        return idTagProvider == null ? emptyList() : idTagProvider.integratedTags();
    }

    public boolean checkReservationId(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_CHECK_RESERVATION, false);
    }

    public List<OcppChargeBox> getChargeBoxesForAlert() {
        return chargeBoxConfigService.getChargeBoxesForAlert(KEY_SKIP_HEARTBEAT_CHECK);
    }

    public boolean skipHeartBeatConfig(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, KEY_SKIP_HEARTBEAT_CONFIG, false);
    }
}

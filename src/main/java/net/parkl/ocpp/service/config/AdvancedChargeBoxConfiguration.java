package net.parkl.ocpp.service.config;

import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
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

        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_START_TIMEOUT_ENABLED, false);
    }

    public int getStartTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_START_TIMEOUT_SECS, 60);
    }

    public boolean isPreparingTimeoutEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_PREPARING_TIMEOUT_ENABLED, false);
    }

    public int getPreparingTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_PREPARING_TIMEOUT_SECS, 60);
    }

    public boolean isTransactionPartialEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED, false);
    }

    public boolean waitingForChargingProcessEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED, false);
    }

    public boolean isStartTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(AdvancedChargeBoxConfigKeys.KEY_START_TIMEOUT_ENABLED) > 0;
    }

    public boolean isPreparingTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(AdvancedChargeBoxConfigKeys.KEY_PREPARING_TIMEOUT_ENABLED) > 0;
    }

    public boolean isUsingIntegratedTag(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_USING_INTEGRATED_IDTAG, false);
    }

    public boolean isIdTagMax10Characters(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_IDTAG_MAX10, false);
    }

    public List<String> getIntegrationIdTags() {
        return idTagProvider == null ? emptyList() : idTagProvider.integratedTags();
    }

    public boolean checkReservationId(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, AdvancedChargeBoxConfigKeys.KEY_CHECK_RESERVATION, false);
    }
}

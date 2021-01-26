package net.parkl.ocpp.service.config;

import net.parkl.ocpp.entities.OcppChargeBoxSpecificConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * OCPP szerver külső konfigurációs komponens
 *
 * @author andor
 */
@Component
public class OcppSpecialConfiguration {

    @Autowired
    private OcppChargeBoxSpecificConfigService chargeBoxConfigService;

    /**
     * Felhasználható Parkl ID tagek
     */
    @Value("${ocpp.integration.idtag:044943121F1D80,100069656E72,100069656E73,100069656E74}")
    @Deprecated
    private String integrationIdTags;


    private boolean getConfigValueAsBool(String chargeBoxId, String key, boolean defaultValue) {
        OcppChargeBoxSpecificConfig config = chargeBoxConfigService.findByChargeBoxIdAndKey(chargeBoxId, key);
        if (config==null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(config.getConfigValue());
    }

    private int getConfigValueAsInt(String chargeBoxId, String key, int defaultValue) {
        OcppChargeBoxSpecificConfig config = chargeBoxConfigService.findByChargeBoxIdAndKey(chargeBoxId, key);
        if (config==null) {
            return defaultValue;
        }
        return Integer.parseInt(config.getConfigValue());
    }

    /**
     * @return Töltés indítás utáni timeout be van-e kapcsolva az OCPP szerveren?  (pl. Alfen kábeles töltő)
     */
    public boolean isStartTimeoutEnabled(String chargeBoxId) {

        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_START_TIMEOUT_ENABLED, false);
    }

    /**
     * @return Töltés indítás utáni timeout másodpercben
     */
    public int getStartTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_START_TIMEOUT_SECS, 60);
    }


    /**
     * Töltés indítás utáni preparing timeout be van-e kapcsolva. Olyan charge boxokra érdemes bekapcsolni, ahol 1 perc után prepairing állapotba marad a töltő
     * pl Ecotap töltő
     */
    public boolean isPreparingTimeoutEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_PREPARING_TIMEOUT_ENABLED, false);
    }
    /**
     * @return Töltés indítás utáni preparing timeout másodpercben
     */
    public int getPreparingTimeoutSecs(String chargeBoxId) {
        return getConfigValueAsInt(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_PREPARING_TIMEOUT_SECS, 60);
    }


    /**
     * Igaz, ha a charge box esetében a transaction stop value partial értéket tartalmaz
     * (a mérőóra nem küld abszolút állást, pl. Schneider)
     */
    public boolean isTransactionPartialEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_TRANSACTION_PARTIAL_ENABLED, false);
    }

    /**
     * Töltés indításkor megvárja-e a tranzakció létrehozáskor az {@link net.parkl.ocpp.entities.OcppChargingProcess} rekord létrejöttét.<br>
     * Olyan töltőknél érdemes bekapcsolni, amelyek túl gyorsan reagálnak StartTransaction üzenettel a RemoteStartTransactionre (pl. Elinta 20ms)
     */
    public boolean isWaitingForChargingProcessEnabled(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED, false);
    }


    public boolean isStartTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(OcppChargeBoxSpecificConfigKeys.KEY_START_TIMEOUT_ENABLED)>0;
    }

    public boolean isPreparingTimeoutEnabledForAny() {
        return chargeBoxConfigService.countByKey(OcppChargeBoxSpecificConfigKeys.KEY_PREPARING_TIMEOUT_ENABLED)>0;
    }

    public boolean isUsingIntegratedTag(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_USING_INTEGRATED_IDTAG, false);
    }
    public boolean isIdTagMax10Characters(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_IDTAG_MAX10, false);
    }


    /**
     * @return Felhasználható Parkl ID tagek
     */
    @Deprecated
    public String getIntegrationIdTags() {
        return integrationIdTags;
    }

    /**
     * Olyan toltokhoz amik kuldik a reservation id-t meg 0-kent es ne dobjunk ra exceptiont
     * @param chargeBoxId
     * @return
     */
    public boolean checkReservationId(String chargeBoxId) {
        return getConfigValueAsBool(chargeBoxId, OcppChargeBoxSpecificConfigKeys.KEY_CHECK_RESERVATION, false);
    }
}

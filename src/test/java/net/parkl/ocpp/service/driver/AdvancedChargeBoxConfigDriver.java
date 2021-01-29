package net.parkl.ocpp.service.driver;

import lombok.NoArgsConstructor;
import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfigService;

@NoArgsConstructor
public class AdvancedChargeBoxConfigDriver {

    private AdvancedChargeBoxConfigService service;

    private String chargeBoxId;
    private String key;
    private String value;

    public static AdvancedChargeBoxConfigDriver createAdvancedChargeBoxDriver(AdvancedChargeBoxConfigService service) {
        AdvancedChargeBoxConfigDriver driver = new AdvancedChargeBoxConfigDriver();
        driver.service = service;
        return driver;
    }

    public void deleteByKey(String key) {
        service.deleteByKey(key);
    }

    public AdvancedChargeBoxConfig findByChargeBoxIdAndKey(String chargeBoxId, String key) {
        return service.findByChargeBoxIdAndKey(chargeBoxId, key);
    }

    public void createConfig() {
        service.saveConfigValue(chargeBoxId, key, value);
    }

    public AdvancedChargeBoxConfigDriver withChargeBoxId(String chargeBoxId){
        this.chargeBoxId = chargeBoxId;
        return this;
    }

    public AdvancedChargeBoxConfigDriver withKey(String key){
        this.key = key;
        return this;
    }

    public AdvancedChargeBoxConfigDriver withValue(String value){
        this.value = value;
        return this;
    }


}

package net.parkl.ocpp.service.config;

import net.parkl.ocpp.entities.OcppChargeBoxSpecificConfig;

import java.util.List;

public interface OcppChargeBoxSpecificConfigService  {
    OcppChargeBoxSpecificConfig findByChargeBoxIdAndKey(String chargeBoxId, String key);

    long countByKey(String key);

    void deleteByKey(String key);

    OcppChargeBoxSpecificConfig saveConfigValue(String chargeBoxId, String key, String value);

    List<OcppChargeBoxSpecificConfig> getAll();

    void delete(int id);

    List<String> getChargeBoxIds();
}

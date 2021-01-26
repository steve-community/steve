package net.parkl.ocpp.service.config;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargeBoxSpecificConfig;
import net.parkl.ocpp.repositories.OcppChargeBoxSpecificConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OcppChargeBoxSpecificConfigServiceImpl implements OcppChargeBoxSpecificConfigService {
    @Autowired
    private OcppChargeBoxSpecificConfigRepository chargeBoxConfigRepository;

    @Override
    public OcppChargeBoxSpecificConfig findByChargeBoxIdAndKey(String chargeBoxId, String key) {
        return chargeBoxConfigRepository.findByChargeBoxIdAndConfigKey(chargeBoxId, key);
    }

    @Override
    public List<OcppChargeBoxSpecificConfig> findByChargeBoxId(String chargeBoxId) {
        return chargeBoxConfigRepository.findByChargeBoxIdOrderByConfigKeyAsc(chargeBoxId);
    }

    @Override
    public long countByKey(String key) {
        return chargeBoxConfigRepository.countByConfigKey(key);
    }

    @Override
    @Transactional
    public void deleteByKey(String key) {
        log.info("Deleting configuration values by key: {}...", key);
        chargeBoxConfigRepository.deleteByConfigKey(key);
    }

    @Override
    @Transactional
    public OcppChargeBoxSpecificConfig saveConfigValue(String chargeBoxId, String key, String value) {
        log.info("Saving configuration value {} for {}: {}...", key, chargeBoxId, value);
        OcppChargeBoxSpecificConfig c = chargeBoxConfigRepository.findByChargeBoxIdAndConfigKey(chargeBoxId, key);
        if (c==null) {
            c = new OcppChargeBoxSpecificConfig();
            c.setChargeBoxId(chargeBoxId);
            c.setConfigKey(key);
        }
        c.setConfigValue(value);
        return chargeBoxConfigRepository.save(c);
    }

    @Override
    public List<OcppChargeBoxSpecificConfig> getAll() {
        return chargeBoxConfigRepository.getAll();
    }

    @Override
    @Transactional
    public void delete(int id) {
        log.info("Deleting configuration values by id: {}...", id);
        chargeBoxConfigRepository.deleteByOcppChargeBoxConfigId(id);
    }

    @Override
    public List<String> getChargeBoxIds() {
        return chargeBoxConfigRepository.getChargeBoxIds();
    }


}

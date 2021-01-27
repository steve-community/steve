package net.parkl.ocpp.service.config;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import net.parkl.ocpp.repositories.AdvancedChargeBoxConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class AdvancedChargeBoxConfigService {
    @Autowired
    private AdvancedChargeBoxConfigRepository repository;

    public AdvancedChargeBoxConfig findByChargeBoxIdAndKey(String chargeBoxId, String key) {
        return repository.findByChargeBoxIdAndConfigKey(chargeBoxId, key);
    }

    public long countByKey(String key) {
        return repository.countByConfigKey(key);
    }

    public void deleteByKey(String key) {
        log.info("Deleting configuration values by key: {}...", key);
        repository.deleteByConfigKey(key);
    }

    public void saveConfigValue(String chargeBoxId, String key, String value) {
        log.info("Saving configuration value {} for {}: {}...", key, chargeBoxId, value);
        AdvancedChargeBoxConfig configKey = repository.findByChargeBoxIdAndConfigKey(chargeBoxId, key);
        if (configKey == null) {
            configKey = new AdvancedChargeBoxConfig();
            configKey.setChargeBoxId(chargeBoxId);
            configKey.setConfigKey(key);
        }
        configKey.setConfigValue(value);
        repository.save(configKey);
    }

    public List<AdvancedChargeBoxConfig> getAll() {
        return repository.getAll();
    }

    public void delete(int id) {
        log.info("Deleting configuration values by id: {}...", id);
        repository.deleteById(id);
    }

    public List<String> getChargeBoxIds() {
        return repository.getChargeBoxIds();
    }


}

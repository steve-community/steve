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

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.AdvancedChargeBoxConfigRepository;
import net.parkl.ocpp.service.cs.ChargePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class AdvancedChargeBoxConfigService {
    @Autowired
    private AdvancedChargeBoxConfigRepository repository;
    @Autowired
    private ChargePointService chargePointService;

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

    public List<AdvancedChargeBoxConfig> findByChargeBoxId(String chargeBoxId) {
        return repository.findByChargeBoxIdOrderByConfigKeyAsc(chargeBoxId);
    }

    public List<OcppChargeBox> getChargeBoxesForAlert(String key) {
        List<OcppChargeBox> allChargeBox = chargePointService.getAllChargeBoxes();
        List<String> skippedChargeBoxIds = getChargeBoxIdsForKey(key);
        return allChargeBox
                .stream()
                .filter(ocppChargeBox -> skippedChargeBoxIds.contains(ocppChargeBox.getChargeBoxId()))
                .collect(Collectors.toList());
    }

    public List<String> getChargeBoxIdsForKey(String key) {
        return repository.getChargeBoxIdsForKey(key);
    }

}

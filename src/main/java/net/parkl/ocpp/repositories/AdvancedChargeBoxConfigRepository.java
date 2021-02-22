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
package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdvancedChargeBoxConfigRepository extends CrudRepository<AdvancedChargeBoxConfig, Integer>{

	List<AdvancedChargeBoxConfig> findByChargeBoxIdOrderByConfigKeyAsc(String chargeBoxId);

	AdvancedChargeBoxConfig findByChargeBoxIdAndConfigKey(String chargeBoxId, String key);

	long countByConfigKey(String key);

	@Modifying
	void deleteByConfigKey(String key);

	@Query("SELECT OBJECT(config) FROM AdvancedChargeBoxConfig AS config")
	List<AdvancedChargeBoxConfig> getAll();

	@Query("SELECT config.chargeBoxId FROM AdvancedChargeBoxConfig AS config GROUP BY config.chargeBoxId")
	List<String> getChargeBoxIds();

	@Query("SELECT config.chargeBoxId FROM AdvancedChargeBoxConfig AS config WHERE config.configKey=?1")
	List<String> getChargeBoxIdsForKey(String key);
}

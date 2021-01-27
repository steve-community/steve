package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.AdvancedChargeBoxConfig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdvancedChargeBoxConfigRepository extends CrudRepository<AdvancedChargeBoxConfig, Integer>{

	AdvancedChargeBoxConfig findByChargeBoxIdAndConfigKey(String chargeBoxId, String key);

	long countByConfigKey(String key);

	@Modifying
	void deleteByConfigKey(String key);

	@Query("SELECT OBJECT(config) FROM AdvancedChargeBoxConfig AS config")
	List<AdvancedChargeBoxConfig> getAll();

	@Query("SELECT config.chargeBoxId FROM AdvancedChargeBoxConfig AS config GROUP BY config.chargeBoxId")
	List<String> getChargeBoxIds();

	@Modifying
	void deleteByOcppChargeBoxConfigId(int id);
}

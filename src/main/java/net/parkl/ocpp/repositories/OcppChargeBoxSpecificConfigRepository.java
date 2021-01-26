package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppChargeBoxSpecificConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OcppChargeBoxSpecificConfigRepository extends CrudRepository<OcppChargeBoxSpecificConfig, Integer>,
		JpaSpecificationExecutor<OcppChargeBoxSpecificConfig> {

	List<OcppChargeBoxSpecificConfig> findByChargeBoxIdOrderByConfigKeyAsc(String chargeBoxId);

	OcppChargeBoxSpecificConfig findByChargeBoxIdAndConfigKey(String chargeBoxId, String key);

	long countByConfigKey(String key);

	@Modifying
	int deleteByConfigKey(String key);

	Page<OcppChargeBoxSpecificConfig> findAll(Specification<OcppChargeBoxSpecificConfig> spec, Pageable pageable);

	@Query("SELECT OBJECT(c) FROM OcppChargeBoxSpecificConfig AS c")
	List<OcppChargeBoxSpecificConfig> getAll();

	@Query("SELECT c.chargeBoxId FROM OcppChargeBoxSpecificConfig AS c GROUP BY c.chargeBoxId")
	List<String> getChargeBoxIds();

	@Modifying
	void deleteByOcppChargeBoxConfigId(int id);
}

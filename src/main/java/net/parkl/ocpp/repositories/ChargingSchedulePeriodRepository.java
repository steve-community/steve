package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.ChargingSchedulePeriod;
import net.parkl.ocpp.entities.OcppChargingProfile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChargingSchedulePeriodRepository extends CrudRepository<ChargingSchedulePeriod, Integer> {
    List<ChargingSchedulePeriod> findByChargingProfile(OcppChargingProfile profile);

    @Modifying
    int deleteByChargingProfile(OcppChargingProfile profile);
}

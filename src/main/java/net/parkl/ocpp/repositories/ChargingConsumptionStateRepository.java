package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.ChargingConsumptionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface ChargingConsumptionStateRepository extends JpaRepository<ChargingConsumptionState, String> {
    @Query(value = "DELETE FROM ChargingConsumptionState AS l WHERE l.createDate<?1")
    @Modifying
    int deleteBefore(Date date);
}

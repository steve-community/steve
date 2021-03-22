package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.ChargingConsumptionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChargingConsumptionStateRepository extends JpaRepository<ChargingConsumptionState, String> {
}

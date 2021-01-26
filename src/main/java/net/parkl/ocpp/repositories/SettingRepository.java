package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Setting;
import org.springframework.data.repository.CrudRepository;

public interface SettingRepository extends CrudRepository<Setting, String>{

}

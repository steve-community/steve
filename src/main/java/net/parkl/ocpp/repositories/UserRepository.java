package net.parkl.ocpp.repositories;


import net.parkl.ocpp.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}

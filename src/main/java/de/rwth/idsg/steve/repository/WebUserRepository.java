package de.rwth.idsg.steve.repository;

import org.springframework.security.provisioning.UserDetailsManager;

public interface WebUserRepository extends UserDetailsManager {

    void changeStatusOfUser(String username, boolean enabled);
}

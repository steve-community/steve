package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public interface UserRepository {
    List<User.Overview> getOverview(UserQueryForm form);
    User.Details getDetails(int userPk);

    void add(UserForm form);
    void update(UserForm form);
    void delete(int userPk);
}

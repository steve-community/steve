package net.parkl.ocpp.service.cs;

import java.util.List;

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.repository.dto.User.Details;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;

public interface UserService {

	void update(UserForm userForm);

	void delete(int userPk);

	void add(UserForm userForm);

	List<User.Overview> getOverview(UserQueryForm form);

	Details getDetails(int userPk);
}

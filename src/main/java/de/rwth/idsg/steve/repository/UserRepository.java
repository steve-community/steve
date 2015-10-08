package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import jooq.steve.db.tables.records.UserRecord;
import org.jooq.Result;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface UserRepository {
    List<User> getUsers(UserQueryForm form);

    Result<UserRecord> getUserRecords();
    Result<UserRecord> getUserRecords(List<String> idTagList);
    UserRecord getUserRecord(String idTag);

    List<String> getUserIdTags();
    List<String> getActiveUserIdTags();

    List<String> getParentIdTags();
    String getParentIdtag(String idTag);

    void addUser(UserForm form);
    void updateUser(UserForm form);
    void deleteUser(String idTag);
}

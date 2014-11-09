package de.rwth.idsg.steve.repository;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.User;
import jooq.steve.db.tables.records.UserRecord;
import ocpp.cp._2012._06.AuthorisationData;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface UserRepository {
    List<User> getUsers();
    void updateUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blockUser) throws SteveException;
    void addUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blocked) throws SteveException;
    void deleteUser(String idTag) throws SteveException;

    /**
     * For OCPP 1.5: Helper method to read ALL user details
     * from the DB for the operation SendLocalList.
     *
     */
    List<AuthorisationData> getAllUserDetails();

    /**
     * For OCPP 1.5: Helper method to read user details of GIVEN idTags
     * from the DB for the operation SendLocalList.
     *
     */
    List<AuthorisationData> getUserDetails(List<String> idTagList);

    Optional<UserRecord> getUserDetails(String idTag);
}
package de.rwth.idsg.steve.repository;

import jooq.steve.db.tables.records.UserRecord;
import org.jooq.Result;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface UserRepository {
    Result<UserRecord> getUserRecords();
    Result<UserRecord> getUserRecords(List<String> idTagList);
    UserRecord getUserRecord(String idTag);

    List<String> getUserIdTags();
    List<String> getActiveUserIdTags();
    String getParentIdtag(String idTag);

    void addUser(String idTag, String parentIdTag, Timestamp expiryTimestamp);
    void updateUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blocked);
    void deleteUser(String idTag);
}
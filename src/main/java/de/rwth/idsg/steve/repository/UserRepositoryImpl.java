package de.rwth.idsg.steve.repository;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.OcppConstants;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2012._06.AuthorizationStatus;
import ocpp.cp._2012._06.IdTagInfo;
import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static jooq.steve.db.tables.User.USER;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Autowired private OcppConstants ocppConstants;

    @Override
    public List<User> getUsers() {
        return getUserRecord().map(new UserMapper());
    }

    /**
     * INSERT IGNORE INTO user (idTag, parentIdTag, expiryDate, blocked) VALUES (?,?,?,?)
     */
    @Override
    public void addUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blocked) throws SteveException {
        try {
            int count = DSL.using(config)
                           .insertInto(USER,
                                   USER.IDTAG, USER.PARENTIDTAG, USER.EXPIRYDATE, USER.BLOCKED)
                           .values(idTag, parentIdTag, expiryTimestamp, blocked)
                           .onDuplicateKeyIgnore() // Important detail
                           .execute();

            if (count == 0) {
                throw new SteveException("A user with idTag '" + idTag + "' already exists.");
            }
        } catch (DataAccessException e) {
            throw new SteveException("Execution of addUser for idTag '" + idTag + "' FAILED.", e);
        }
    }

    /**
     * UPDATE user
     * SET parentIdTag = ?, expiryDate = ?, blocked = ?
     * WHERE idTag = ?
     */
    @Override
    public void updateUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blocked) throws SteveException {
        try {
            DSL.using(config)
               .update(USER)
               .set(USER.PARENTIDTAG, parentIdTag)
               .set(USER.EXPIRYDATE, expiryTimestamp)
               .set(USER.BLOCKED, blocked)
               .where(USER.IDTAG.equal(idTag))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("Execution of updateUser for idTag '" + idTag + "' FAILED.", e);
        }
    }

    /**
     * DELETE FROM user
     * WHERE idTag = ?
     */
    @Override
    public void deleteUser(String idTag) throws SteveException {
        try {
            DSL.using(config)
               .delete(USER)
               .where(USER.IDTAG.equal(idTag))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("Execution of deleteUser for idTag '" + idTag + "' FAILED.", e);
        }
    }

    @Override
    public List<AuthorisationData> getAuthDataOfAllUsers() {
        return getUserRecord().map(new AuthorisationDataMapper());
    }

    /**
     * SELECT *
     * FROM user
     * WHERE idTag IN (?,?,...,?)
     */
    @Override
    public List<AuthorisationData> getAuthData(List<String> idTagList) {
        return DSL.using(config)
                  .selectFrom(USER)
                  .where(USER.IDTAG.in(idTagList))
                  .fetch()
                  .map(new AuthorisationDataMapper());
    }

    /**
     * SELECT *
     * FROM user
     * WHERE idTag = ?
     */
    @Override
    public Optional<UserRecord> getUserDetails(String idTag) {
        UserRecord record = DSL.using(config)
                               .selectFrom(USER)
                               .where(USER.IDTAG.equal(idTag))
                               .fetchOne();

        if (record == null) {
            return Optional.absent();
        } else {
            return Optional.of(record);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * SELECT *
     * FROM user
     */
    private Result<UserRecord> getUserRecord() {
        return DSL.using(config)
                  .selectFrom(USER)
                  .fetch();
    }

    private class UserMapper implements RecordMapper<UserRecord, User> {
        @Override
        public User map(UserRecord r) {
            return User.builder()
                    .idTag(r.getIdtag())
                    .parentIdTag(r.getParentidtag())
                    .expiryDate(DateTimeUtils.humanize(r.getExpirydate()))
                    .inTransaction(r.getIntransaction())
                    .blocked(r.getBlocked())
                    .build();
        }
    }

    private class AuthorisationDataMapper implements RecordMapper<UserRecord, AuthorisationData> {

        DateTime nowDt = new DateTime();
        DateTime cacheExpiry = nowDt.plus(ocppConstants.getHoursToExpire());
        Timestamp now = new Timestamp(nowDt.getMillis());

        @Override
        public AuthorisationData map(UserRecord record) {

            String idTag = record.getIdtag();
            String parentIdTag = record.getParentidtag();
            Timestamp expiryDate = record.getExpirydate();

            // Create IdTagInfo of an idTag
            IdTagInfo idTagInfo = new IdTagInfo();
            AuthorizationStatus authStatus;

            if (record.getIntransaction()) {
                authStatus = AuthorizationStatus.CONCURRENT_TX;

            } else if (record.getBlocked()) {
                authStatus = AuthorizationStatus.BLOCKED;

            } else if (expiryDate != null && now.after(expiryDate)) {
                authStatus = AuthorizationStatus.EXPIRED;

            } else {
                authStatus = AuthorizationStatus.ACCEPTED;
                // When accepted, set the additional fields
                idTagInfo.setExpiryDate(cacheExpiry);
                if (parentIdTag != null) {
                    idTagInfo.setParentIdTag(parentIdTag);
                }
            }
            idTagInfo.setStatus(authStatus);

            return new AuthorisationData().withIdTag(idTag).withIdTagInfo(idTagInfo);
        }
    }
}
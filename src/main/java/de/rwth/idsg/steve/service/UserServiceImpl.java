package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import jooq.steve.db.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cs._2010._08.AuthorizationStatus;
import org.joda.time.DateTime;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired private SettingsRepository settingsRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public List<AuthorisationData> getAuthDataOfAllUsers() {
        int hoursToExpire = settingsRepository.getHoursToExpire();

        return userRepository.getUserRecords()
                             .map(new AuthorisationDataMapper(hoursToExpire));
    }

    @Override
    public List<AuthorisationData> getAuthData(List<String> idTagList) {
        int hoursToExpire = settingsRepository.getHoursToExpire();

        return userRepository.getUserRecords(idTagList)
                             .map(new AuthorisationDataMapper(hoursToExpire));
    }

    @Override
    public ocpp.cs._2010._08.IdTagInfo getIdTagInfoV12(String idTag) {
        UserRecord record = userRepository.getUserRecord(idTag);
        ocpp.cs._2010._08.IdTagInfo idTagInfo = new ocpp.cs._2010._08.IdTagInfo();

        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(AuthorizationStatus.INVALID);
        } else {
            if (record.getIntransaction()) {
                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.CONCURRENT_TX);

            } else if (record.getBlocked()) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.BLOCKED);

            } else if (record.getExpirydate() != null && DateTime.now().isAfter(record.getExpirydate())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.EXPIRED);

            } else {
                log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.ACCEPTED);

                int hours = settingsRepository.getHoursToExpire();
                idTagInfo.setExpiryDate(DateTime.now().plusHours(hours));
                idTagInfo.setParentIdTag(record.getParentidtag());
            }
        }
        return idTagInfo;
    }

    @Override
    public ocpp.cs._2012._06.IdTagInfo getIdTagInfoV15(String idTag) {
        UserRecord record = userRepository.getUserRecord(idTag);
        ocpp.cs._2012._06.IdTagInfo idTagInfo = new ocpp.cs._2012._06.IdTagInfo();

        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.INVALID);
        } else {
            if (record.getIntransaction()) {
                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.CONCURRENT_TX);

            } else if (record.getBlocked()) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.BLOCKED);

            } else if (record.getExpirydate() != null && DateTime.now().isAfter(record.getExpirydate())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.EXPIRED);

            } else {
                log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED);

                int hours = settingsRepository.getHoursToExpire();
                idTagInfo.setExpiryDate(DateTime.now().plusHours(hours));
                idTagInfo.setParentIdTag(record.getParentidtag());
            }
        }
        return idTagInfo;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private class AuthorisationDataMapper implements RecordMapper<UserRecord, AuthorisationData> {
        private final DateTime nowDt;
        private final DateTime cacheExpiry;

        AuthorisationDataMapper(int hoursToExpire) {
            this.nowDt = DateTime.now();
            this.cacheExpiry = nowDt.plus(hoursToExpire);
        }

        @Override
        public AuthorisationData map(UserRecord record) {

            String idTag = record.getIdtag();
            String parentIdTag = record.getParentidtag();
            DateTime expiryDate = record.getExpirydate();

            // Create IdTagInfo of an idTag
            ocpp.cp._2012._06.IdTagInfo idTagInfo = new ocpp.cp._2012._06.IdTagInfo();
            ocpp.cp._2012._06.AuthorizationStatus authStatus;

            if (record.getIntransaction()) {
                authStatus = ocpp.cp._2012._06.AuthorizationStatus.CONCURRENT_TX;

            } else if (record.getBlocked()) {
                authStatus = ocpp.cp._2012._06.AuthorizationStatus.BLOCKED;

            } else if (expiryDate != null && nowDt.isAfter(expiryDate)) {
                authStatus = ocpp.cp._2012._06.AuthorizationStatus.EXPIRED;

            } else {
                authStatus = ocpp.cp._2012._06.AuthorizationStatus.ACCEPTED;
                // When accepted, set the additional fields
                idTagInfo.setExpiryDate(cacheExpiry);
                idTagInfo.setParentIdTag(parentIdTag);
            }
            idTagInfo.setStatus(authStatus);

            return new AuthorisationData().withIdTag(idTag).withIdTagInfo(idTagInfo);
        }
    }

}

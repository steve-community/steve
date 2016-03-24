package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import jooq.steve.db.tables.records.OcppTagRecord;
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
public class OcppTagServiceImpl implements OcppTagService {

    @Autowired private SettingsRepository settingsRepository;
    @Autowired private OcppTagRepository ocppTagRepository;

    @Override
    public List<AuthorisationData> getAuthDataOfAllTags() {
        int hoursToExpire = settingsRepository.getHoursToExpire();

        return ocppTagRepository.getRecords()
                                .map(new AuthorisationDataMapper(hoursToExpire));
    }

    @Override
    public List<AuthorisationData> getAuthData(List<String> idTagList) {
        int hoursToExpire = settingsRepository.getHoursToExpire();

        return ocppTagRepository.getRecords(idTagList)
                                .map(new AuthorisationDataMapper(hoursToExpire));
    }

    @Override
    public ocpp.cs._2010._08.IdTagInfo getIdTagInfoV12(String idTag) {
        OcppTagRecord record = ocppTagRepository.getRecord(idTag);
        ocpp.cs._2010._08.IdTagInfo idTagInfo = new ocpp.cs._2010._08.IdTagInfo();

        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(AuthorizationStatus.INVALID);
        } else {
            if (record.getBlocked()) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.BLOCKED);

//            } else if (record.getInTransaction()) {
//                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
//                idTagInfo.setStatus(AuthorizationStatus.CONCURRENT_TX);

            } else if (record.getExpiryDate() != null && DateTime.now().isAfter(record.getExpiryDate())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.EXPIRED);

            } else {
                log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.ACCEPTED);

                int hours = settingsRepository.getHoursToExpire();
                idTagInfo.setExpiryDate(DateTime.now().plusHours(hours));
                idTagInfo.setParentIdTag(record.getParentIdTag());
            }
        }
        return idTagInfo;
    }

    @Override
    public ocpp.cs._2012._06.IdTagInfo getIdTagInfoV15(String idTag) {
        OcppTagRecord record = ocppTagRepository.getRecord(idTag);
        ocpp.cs._2012._06.IdTagInfo idTagInfo = new ocpp.cs._2012._06.IdTagInfo();

        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.INVALID);
        } else {
            if (record.getBlocked()) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.BLOCKED);

//            } else if (record.getInTransaction()) {
//                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
//                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.CONCURRENT_TX);

            } else if (record.getExpiryDate() != null && DateTime.now().isAfter(record.getExpiryDate())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.EXPIRED);

            } else {
                log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED);

                int hours = settingsRepository.getHoursToExpire();
                idTagInfo.setExpiryDate(DateTime.now().plusHours(hours));
                idTagInfo.setParentIdTag(record.getParentIdTag());
            }
        }
        return idTagInfo;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static class AuthorisationDataMapper implements RecordMapper<OcppTagRecord, AuthorisationData> {
        private final DateTime nowDt;
        private final DateTime cacheExpiry;

        AuthorisationDataMapper(int hoursToExpire) {
            this.nowDt = DateTime.now();
            this.cacheExpiry = nowDt.plus(hoursToExpire);
        }

        @Override
        public AuthorisationData map(OcppTagRecord record) {

            String idTag = record.getIdTag();
            String parentIdTag = record.getParentIdTag();
            DateTime expiryDate = record.getExpiryDate();

            // Create IdTagInfo of an idTag
            ocpp.cp._2012._06.IdTagInfo idTagInfo = new ocpp.cp._2012._06.IdTagInfo();
            ocpp.cp._2012._06.AuthorizationStatus authStatus;

            if (record.getInTransaction()) {
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

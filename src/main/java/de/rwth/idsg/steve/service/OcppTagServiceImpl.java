package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import jooq.steve.db.tables.records.OcppTagRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
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

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    @Override
    public List<AuthorizationData> getAuthDataOfAllTags() {
        return ocppTagRepository.getRecords()
                                .map(new AuthorisationDataMapper());
    }

    @Override
    public List<AuthorizationData> getAuthData(List<String> idTagList) {
        return ocppTagRepository.getRecords(idTagList)
                                .map(new AuthorisationDataMapper());
    }

    @Override
    public List<UnidentifiedIncomingObject> getUnknownOcppTags() {
        return invalidOcppTagService.getObjects();
    }

    @Override
    public IdTagInfo getIdTagInfo(String idTag) {
        OcppTagRecord record = ocppTagRepository.getRecord(idTag);
        IdTagInfo idTagInfo = new IdTagInfo();

        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            idTagInfo.setStatus(AuthorizationStatus.INVALID);
            invalidOcppTagService.processNewUnidentified(idTag);
        } else {

            DateTime expiryDate = record.getExpiryDate();
            boolean isExpiryDateSet = expiryDate != null;

            if (record.getBlocked()) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.BLOCKED);

//            } else if (record.getInTransaction()) {
//                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
//                idTagInfo.setStatus(ocpp.cs._2012._06.AuthorizationStatus.CONCURRENT_TX);

            } else if (isExpiryDateSet && DateTime.now().isAfter(record.getExpiryDate())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.EXPIRED);

            } else {
                log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
                idTagInfo.setStatus(AuthorizationStatus.ACCEPTED);

                // If the database contains an actual expiry, use it. Otherwise, calculate an expiry for cached info
                DateTime expiry = isExpiryDateSet ? expiryDate : DateTime.now().plusHours(settingsRepository.getHoursToExpire());

                idTagInfo.setExpiryDate(expiry);
                idTagInfo.setParentIdTag(record.getParentIdTag());
            }
        }
        return idTagInfo;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class AuthorisationDataMapper implements RecordMapper<OcppTagRecord, AuthorizationData> {

        private final DateTime nowDt = DateTime.now();

        @Override
        public AuthorizationData map(OcppTagRecord record) {
            String idTag = record.getIdTag();
            String parentIdTag = record.getParentIdTag();
            DateTime expiryDate = record.getExpiryDate();

            // Create IdTagInfo of an idTag
            ocpp.cp._2015._10.IdTagInfo idTagInfo = new ocpp.cp._2015._10.IdTagInfo();
            ocpp.cp._2015._10.AuthorizationStatus authStatus;

            if (record.getInTransaction()) {
                authStatus = ocpp.cp._2015._10.AuthorizationStatus.CONCURRENT_TX;

            } else if (record.getBlocked()) {
                authStatus = ocpp.cp._2015._10.AuthorizationStatus.BLOCKED;

            } else if (expiryDate != null && nowDt.isAfter(expiryDate)) {
                authStatus = ocpp.cp._2015._10.AuthorizationStatus.EXPIRED;

            } else {
                authStatus = ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED;
                // When accepted, set the additional fields
                idTagInfo.setExpiryDate(expiryDate);
                idTagInfo.setParentIdTag(parentIdTag);
            }
            idTagInfo.setStatus(authStatus);

            return new AuthorizationData().withIdTag(idTag).withIdTagInfo(idTagInfo);
        }
    }

}

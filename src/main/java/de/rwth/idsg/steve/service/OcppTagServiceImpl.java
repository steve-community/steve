package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
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
    @Autowired private TransactionRepository transactionRepository;

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
    public IdTagInfo getIdTagInfo(String idTag, String askingChargeBoxId) {
        OcppTagRecord record = ocppTagRepository.getRecord(idTag);
        AuthorizationStatus status = decideStatus(record, askingChargeBoxId);

        switch (status) {
            case INVALID:
                log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
                invalidOcppTagService.processNewUnidentified(idTag);
                return new IdTagInfo().withStatus(status);

            case BLOCKED:
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                return new IdTagInfo().withStatus(status)
                                      .withParentIdTag(record.getParentIdTag());

            case EXPIRED:
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                return new IdTagInfo().withStatus(status)
                                      .withParentIdTag(record.getParentIdTag());

            case CONCURRENT_TX:
                log.warn("The user with idTag '{}' is ALREADY in another transaction.", idTag);
                return new IdTagInfo().withStatus(status)
                                      .withParentIdTag(record.getParentIdTag())
                                      .withExpiryDate(getExpiryDateOrDefault(record));

            case ACCEPTED:
                log.debug("The user with idTag '{}' is ACCEPTED.", record.getIdTag());
                return new IdTagInfo().withStatus(status)
                                      .withParentIdTag(record.getParentIdTag())
                                      .withExpiryDate(getExpiryDateOrDefault(record));
            default:
                throw new SteveException("Unexpected AuthorizationStatus");
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private AuthorizationStatus decideStatus(OcppTagRecord record, String askingChargeBoxId) {
        if (record == null) {
            return AuthorizationStatus.INVALID;
        }

        if (record.getBlocked()) {
            return AuthorizationStatus.BLOCKED;
        }

        if (isExpired(record)) {
            return AuthorizationStatus.EXPIRED;
        }

        // https://github.com/RWTH-i5-IDSG/steve/issues/73
        if (record.getInTransaction()) {
            List<String> txChargeBoxIds = transactionRepository.getChargeBoxIdsOfActiveTransactions(record.getIdTag());
            if (!txChargeBoxIds.contains(askingChargeBoxId)) {
                return AuthorizationStatus.CONCURRENT_TX;
            }
        }

        return AuthorizationStatus.ACCEPTED;
    }

    /**
     * If the database contains an actual expiry, use it. Otherwise, calculate an expiry for cached info
     */
    private DateTime getExpiryDateOrDefault(OcppTagRecord record) {
        if (record.getExpiryDate() != null) {
            return record.getExpiryDate();
        } else {
            return DateTime.now().plusHours(settingsRepository.getHoursToExpire());
        }
    }

    private static boolean isExpired(OcppTagRecord record) {
        DateTime expiry = record.getExpiryDate();
        return expiry != null && DateTime.now().isAfter(expiry);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class AuthorisationDataMapper implements RecordMapper<OcppTagRecord, AuthorizationData> {

        private final DateTime nowDt = DateTime.now();

        @Override
        public AuthorizationData map(OcppTagRecord record) {
            DateTime expiryDate = record.getExpiryDate();

            ocpp.cp._2015._10.IdTagInfo idTagInfo;

            if (record.getBlocked()) {
                idTagInfo = new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.BLOCKED)
                                                             .withParentIdTag(record.getParentIdTag());

            } else if (expiryDate != null && nowDt.isAfter(expiryDate)) {
                idTagInfo = new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.EXPIRED)
                                                             .withParentIdTag(record.getParentIdTag());

            } else if (record.getInTransaction()) {
                idTagInfo = new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.CONCURRENT_TX)
                                                             .withParentIdTag(record.getParentIdTag())
                                                             .withExpiryDate(expiryDate);
            } else {
                idTagInfo = new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED)
                                                             .withParentIdTag(record.getParentIdTag())
                                                             .withExpiryDate(expiryDate);
            }

            return new AuthorizationData().withIdTag(record.getIdTag()).withIdTagInfo(idTagInfo);
        }
    }

}

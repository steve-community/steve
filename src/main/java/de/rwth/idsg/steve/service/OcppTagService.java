/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.01.2015
 */
@Slf4j
@Service
public class OcppTagService {

    @Autowired private SettingsRepository settingsRepository;
    @Autowired private OcppTagRepository ocppTagRepository;

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    public List<AuthorizationData> getAuthDataOfAllTags() {
        return ocppTagRepository.getRecords()
                                .map(new AuthorisationDataMapper());
    }

    public List<AuthorizationData> getAuthData(List<String> idTagList) {
        return ocppTagRepository.getRecords(idTagList)
                                .map(new AuthorisationDataMapper());
    }

    public List<UnidentifiedIncomingObject> getUnknownOcppTags() {
        return invalidOcppTagService.getObjects();
    }

    public void removeUnknown(String idTag) {
        invalidOcppTagService.remove(idTag);
    }

    public void removeUnknown(List<String> idTagList) {
        invalidOcppTagService.removeAll(idTagList);
    }

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext) {
        if (Strings.isNullOrEmpty(idTag)) {
            return null;
        }

        OcppTagActivityRecord record = ocppTagRepository.getRecord(idTag);
        AuthorizationStatus status = decideStatus(record, idTag, isStartTransactionReqContext);

        switch (status) {
            case INVALID:
                invalidOcppTagService.processNewUnidentified(idTag);
                return new IdTagInfo().withStatus(status);

            case BLOCKED:
            case EXPIRED:
            case CONCURRENT_TX:
            case ACCEPTED:
                return new IdTagInfo().withStatus(status)
                                      .withParentIdTag(record.getParentIdTag())
                                      .withExpiryDate(getExpiryDateOrDefault(record));
            default:
                throw new SteveException("Unexpected AuthorizationStatus");
        }
    }

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext, Supplier<IdTagInfo> supplierWhenException) {
        try {
            return getIdTagInfo(idTag, isStartTransactionReqContext);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return supplierWhenException.get();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * If the database contains an actual expiry, use it. Otherwise, calculate an expiry for cached info
     */
    @Nullable
    private DateTime getExpiryDateOrDefault(OcppTagActivityRecord record) {
        if (record.getExpiryDate() != null) {
            return record.getExpiryDate();
        }

        int hoursToExpire = settingsRepository.getHoursToExpire();

        // From web page: The value 0 disables this functionality (i.e. no expiry date will be set).
        if (hoursToExpire == 0) {
            return null;
        } else {
            return DateTime.now().plusHours(hoursToExpire);
        }
    }

    private AuthorizationStatus decideStatus(OcppTagActivityRecord record, String idTag, boolean isStartTransactionReqContext) {
        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            return AuthorizationStatus.INVALID;
        }

        if (isBlocked(record)) {
            log.error("The user with idTag '{}' is BLOCKED.", idTag);
            return AuthorizationStatus.BLOCKED;
        }

        if (isExpired(record, DateTime.now())) {
            log.error("The user with idTag '{}' is EXPIRED.", idTag);
            return AuthorizationStatus.EXPIRED;
        }

        // https://github.com/RWTH-i5-IDSG/steve/issues/219
        if (isStartTransactionReqContext && reachedLimitOfActiveTransactions(record)) {
            log.warn("The user with idTag '{}' is ALREADY in another transaction(s).", idTag);
            return AuthorizationStatus.CONCURRENT_TX;
        }

        log.debug("The user with idTag '{}' is ACCEPTED.", record.getIdTag());
        return AuthorizationStatus.ACCEPTED;
    }

    /**
     * ConcurrentTx is only valid for StartTransactionRequest
     */
    private static ocpp.cp._2015._10.AuthorizationStatus decideStatusForAuthData(OcppTagActivityRecord record, DateTime now) {
        if (isBlocked(record)) {
            return ocpp.cp._2015._10.AuthorizationStatus.BLOCKED;
        } else if (isExpired(record, now)) {
            return ocpp.cp._2015._10.AuthorizationStatus.EXPIRED;
//        } else if (reachedLimitOfActiveTransactions(record)) {
//            return ocpp.cp._2015._10.AuthorizationStatus.CONCURRENT_TX;
        } else {
            return ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED;
        }
    }

    private static boolean isExpired(OcppTagActivityRecord record, DateTime now) {
        DateTime expiry = record.getExpiryDate();
        return expiry != null && now.isAfter(expiry);
    }

    private static boolean isBlocked(OcppTagActivityRecord record) {
        return getToggle(record) == ConcurrencyToggle.Blocked;
    }

    private static boolean reachedLimitOfActiveTransactions(OcppTagActivityRecord record) {
        ConcurrencyToggle toggle = getToggle(record);
        switch (toggle) {
            case Blocked:
                return true; // for completeness
            case AllowAll:
                return false;
            case AllowAsSpecified:
                int max = record.getMaxActiveTransactionCount();
                long active = record.getActiveTransactionCount();
                return active >= max;
            default:
                throw new RuntimeException("Unexpected ConcurrencyToggle");
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class AuthorisationDataMapper implements RecordMapper<OcppTagActivityRecord, AuthorizationData> {

        private final DateTime nowDt = DateTime.now();

        @Override
        public AuthorizationData map(OcppTagActivityRecord record) {
            return new AuthorizationData().withIdTag(record.getIdTag())
                                          .withIdTagInfo(
                                                  new ocpp.cp._2015._10.IdTagInfo()
                                                          .withStatus(decideStatusForAuthData(record, nowDt))
                                                          .withParentIdTag(record.getParentIdTag())
                                                          .withExpiryDate(record.getExpiryDate())
                                          );
        }
    }

    private enum ConcurrencyToggle {
        Blocked, AllowAll, AllowAsSpecified
    }

    private static ConcurrencyToggle getToggle(OcppTagActivityRecord r) {
        int max = r.getMaxActiveTransactionCount();
        if (max == 0) {
            return ConcurrencyToggle.Blocked;
        } else if (max < 0) {
            return ConcurrencyToggle.AllowAll;
        } else {
            return ConcurrencyToggle.AllowAsSpecified;
        }
    }
}

/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.01.2015
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcppTagService {

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    private final SettingsRepository settingsRepository;
    private final OcppTagRepository ocppTagRepository;

    public List<OcppTag.Overview> getOverview(OcppTagQueryForm form) {
        return ocppTagRepository.getOverview(form);
    }

    public OcppTagActivityRecord getRecord(int ocppTagPk) {
        return ocppTagRepository.getRecord(ocppTagPk);
    }

    public List<String> getIdTags() {
        return ocppTagRepository.getIdTags();
    }

    public List<String> getActiveIdTags() {
        return ocppTagRepository.getActiveIdTags();
    }

    public List<String> getParentIdTags() {
        return ocppTagRepository.getParentIdTags();
    }

    public String getParentIdtag(String idTag) {
        return ocppTagRepository.getParentIdtag(idTag);
    }

    public List<AuthorizationData> getAuthDataOfAllTags() {
        DateTime nowDt = DateTime.now();
        return ocppTagRepository.getRecords().map(record -> mapToAuthorizationData(record, nowDt));
    }

    public List<AuthorizationData> getAuthData(List<String> idTagList) {
        DateTime nowDt = DateTime.now();
        return ocppTagRepository.getRecords(idTagList).map(record -> mapToAuthorizationData(record, nowDt));
    }

    public List<UnidentifiedIncomingObject> getUnknownOcppTags() {
        return invalidOcppTagService.getObjects();
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
    // Create, Update, Delete operations
    // -------------------------------------------------------------------------

    public int addOcppTag(OcppTagForm form) {
        var id = ocppTagRepository.addOcppTag(form);
        removeUnknown(Collections.singletonList(form.getIdTag()));
        return id;
    }
    public void addOcppTagList(List<String> idTagList) {
        ocppTagRepository.addOcppTagList(idTagList);
        removeUnknown(idTagList);
    }

    public void updateOcppTag(OcppTagForm form) {
        ocppTagRepository.updateOcppTag(form);
    }

    public void deleteOcppTag(int ocppTagPk) {
        ocppTagRepository.deleteOcppTag(ocppTagPk);
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

        // https://github.com/steve-community/steve/issues/219
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
        return record.getMaxActiveTransactionCount() == 0;
    }

    private static boolean reachedLimitOfActiveTransactions(OcppTagActivityRecord record) {
        int max = record.getMaxActiveTransactionCount();

        // blocked
        if (max == 0) {
            return true;
        }

        // allow all
        if (max < 0) {
            return false;
        }

        // allow as specified
        return record.getActiveTransactionCount() >= max;
    }

    private static AuthorizationData mapToAuthorizationData(OcppTagActivityRecord record, DateTime nowDt) {
        return new AuthorizationData().withIdTag(record.getIdTag())
                                      .withIdTagInfo(
                                              new ocpp.cp._2015._10.IdTagInfo()
                                                      .withStatus(decideStatusForAuthData(record, nowDt))
                                                      .withParentIdTag(record.getParentIdTag())
                                                      .withExpiryDate(record.getExpiryDate())
                                      );
    }
}

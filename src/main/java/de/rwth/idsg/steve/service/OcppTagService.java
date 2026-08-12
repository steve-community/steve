/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.service.dto.AuthTagContext;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isBlocked;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isExpired;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.01.2015
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcppTagService {

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    private final OcppTagRepository ocppTagRepository;
    private final AuthTagService authTagService;

    public List<OcppTag.OcppTagOverview> getOverview(OcppTagQueryForm form) {
        return ocppTagRepository.getOverview(form);
    }

    public OcppTagActivityRecord getRecord(int ocppTagPk) {
        return ocppTagRepository.getRecord(ocppTagPk);
    }

    public List<String> getIdTags() {
        return ocppTagRepository.getIdTags();
    }

    public List<String> getIdTags(List<String> idTagList) {
        return ocppTagRepository.getIdTags(idTagList);
    }

    public List<String> getIdTagsWithoutUser() {
        return ocppTagRepository.getIdTagsWithoutUser();
    }

    public List<String> getActiveIdTags() {
        return ocppTagRepository.getActiveIdTags();
    }

    public List<String> getParentIdTags() {
        return ocppTagRepository.getParentIdTags();
    }

    public String getParentIdtag(String idTag) {
        var map = ocppTagRepository.getParentIdTags(Set.of(idTag));
        return map.get(idTag);
    }

    /**
     * Checks whether two distinct idTags are related through {@code parentIdTag}. Exact idTag equality is handled by
     * the caller before invoking this method.
     * <pre>
     * Relationship                           Example                          Result
     * ------------------------------------------------------------------------------
     * Same non-null parent                   C1.parent=P, C2.parent=P         true
     * First tag is the second tag's parent   P.parent=null, C.parent=P        true
     * Second tag is the first tag's parent   C.parent=P, P.parent=null        true
     * Different parents                      C1.parent=P1, C2.parent=P2       false
     * Both tags are parentless               P1.parent=null, P2.parent=null   false
     * Either tag is unknown                  No database record               false
     * </pre>
     */
    public boolean areIdTagsRelatedByParent(@NotNull String idTag1, @NotNull String idTag2) {
        var parents = ocppTagRepository.getParentIdTags(Set.of(idTag1, idTag2));
        var parent1 = parents.get(idTag1);
        var parent2 = parents.get(idTag2);

        return (parent1 != null && parent1.equals(parent2))
            || idTag1.equals(parent2)
            || idTag2.equals(parent1);
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

    public IdTagInfo getIdTagInfo(String idTag, AuthTagContext authTagContext,
                                  String chargeBoxId, @Nullable Integer connectorId) {
        IdTagInfo idTagInfo = authTagService.decideStatus(idTag, authTagContext, chargeBoxId, connectorId);

        if (idTagInfo.getStatus() == AuthorizationStatus.INVALID) {
            invalidOcppTagService.processNewUnidentified(idTag);
        }

        return idTagInfo;
    }

    @Nullable
    public IdTagInfo getIdTagInfo(String idTag, AuthTagContext authTagContext,
                                  String chargeBoxId, @Nullable Integer connectorId,
                                  Supplier<IdTagInfo> supplierWhenException) {
        try {
            return getIdTagInfo(idTag, authTagContext, chargeBoxId, connectorId);
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
        var details = getRecord(ocppTagPk);
        ocppTagRepository.deleteOcppTag(ocppTagPk);
        log.info("Deleted Ocpp Tag with ocppTagPk={} and ocppTagId={}", ocppTagPk, details.getIdTag());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

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

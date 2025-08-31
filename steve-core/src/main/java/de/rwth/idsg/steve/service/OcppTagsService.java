/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.repository.dto.OcppTagActivity;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
public class OcppTagsService {

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    private final OcppTagRepository ocppTagRepository;
    private final AuthTagService authTagService;

    public List<OcppTag.OcppTagOverview> getOverview(OcppTagQueryForm form) {
        return ocppTagRepository.getOverview(form);
    }

    public OcppTagActivity getRecord(int ocppTagPk) {
        return ocppTagRepository.getRecord(ocppTagPk).orElse(null);
    }

    public List<String> getIdTags() {
        return ocppTagRepository.getIdTags();
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
        return ocppTagRepository.getParentIdTag(idTag);
    }

    public List<AuthorizationData> getAuthDataOfAllTags() {
        var now = Instant.now();
        return ocppTagRepository.getRecords().stream()
                .map(tag -> tag.mapToAuthorizationData(now))
                .toList();
    }

    public List<AuthorizationData> getAuthData(List<String> idTagList) {
        var now = Instant.now();
        return ocppTagRepository.getRecords(idTagList).stream()
                .map(tag -> tag.mapToAuthorizationData(now))
                .toList();
    }

    public List<UnidentifiedIncomingObject> getUnknownOcppTags() {
        return invalidOcppTagService.getObjects();
    }

    public void removeUnknown(List<String> idTagList) {
        invalidOcppTagService.removeAll(idTagList);
    }

    public @Nullable IdTagInfo getIdTagInfo(
            @Nullable String idTag,
            boolean isStartTransactionReqContext,
            @Nullable String chargeBoxId,
            @Nullable Integer connectorId) {
        if (Strings.isNullOrEmpty(idTag)) {
            return null;
        }

        IdTagInfo idTagInfo =
                authTagService.decideStatus(idTag, isStartTransactionReqContext, chargeBoxId, connectorId);

        if (idTagInfo.getStatus() == AuthorizationStatus.INVALID) {
            invalidOcppTagService.processNewUnidentified(idTag);
        }

        return idTagInfo;
    }

    public @Nullable IdTagInfo getIdTagInfo(
            @Nullable String idTag,
            boolean isStartTransactionReqContext,
            @Nullable String chargeBoxId,
            @Nullable Integer connectorId,
            Supplier<IdTagInfo> supplierWhenException) {
        try {
            return getIdTagInfo(idTag, isStartTransactionReqContext, chargeBoxId, connectorId);
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
}

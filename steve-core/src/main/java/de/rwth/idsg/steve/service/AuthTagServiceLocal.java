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

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.OcppTagActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toOffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTagServiceLocal implements AuthTagService {

    private final OcppTagRepository ocppTagRepository;
    private final SettingsRepository settingsRepository;

    @Override
    public IdTagInfo decideStatus(
            String idTag,
            boolean isStartTransactionReqContext,
            @Nullable String chargeBoxId,
            @Nullable Integer connectorId) {
        var tagOpt = ocppTagRepository.getRecord(idTag);
        if (tagOpt.isEmpty()) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            return new IdTagInfo().withStatus(AuthorizationStatus.INVALID);
        }

        var tag = tagOpt.get();
        if (tag.isBlocked()) {
            log.error("The user with idTag '{}' is BLOCKED.", idTag);
            return buildIdTagInfo(tag, AuthorizationStatus.BLOCKED);
        }

        if (tag.isExpired(Instant.now())) {
            log.error("The user with idTag '{}' is EXPIRED.", idTag);
            return buildIdTagInfo(tag, AuthorizationStatus.EXPIRED);
        }

        // https://github.com/steve-community/steve/issues/219
        if (isStartTransactionReqContext && tag.hasReachedLimitOfActiveTransactions()) {
            log.warn("The user with idTag '{}' is ALREADY in another transaction(s).", idTag);
            return buildIdTagInfo(tag, AuthorizationStatus.CONCURRENT_TX);
        }

        log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
        return buildIdTagInfo(tag, AuthorizationStatus.ACCEPTED);
    }

    private IdTagInfo buildIdTagInfo(OcppTagActivity tag, AuthorizationStatus status) {
        return new IdTagInfo()
                .withStatus(status)
                .withParentIdTag(tag.getParentIdTag())
                .withExpiryDate(getExpiryDateOrDefault(tag));
    }

    /**
     * If the database contains an actual expiry, use it. Otherwise, calculate an expiry for cached info
     */
    private @Nullable OffsetDateTime getExpiryDateOrDefault(OcppTagActivity tag) {
        if (tag.getExpiryDate() != null) {
            return toOffsetDateTime(tag.getExpiryDate());
        }

        int hoursToExpire = settingsRepository.getHoursToExpire();

        // From web page: The value 0 disables this functionality (i.e. no expiry date will be set).
        if (hoursToExpire == 0) {
            return null;
        }
        return OffsetDateTime.now().plusHours(hoursToExpire);
    }
}

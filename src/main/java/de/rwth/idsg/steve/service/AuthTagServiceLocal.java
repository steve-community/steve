/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isBlocked;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isExpired;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.reachedLimitOfActiveTransactions;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTagServiceLocal implements AuthTagService {

    private final OcppTagRepository ocppTagRepository;
    private final SettingsRepository settingsRepository;

    @Override
    public IdTagInfo decideStatus(String idTag, boolean isStartTransactionReqContext,
                                  @Nullable String chargeBoxId, @Nullable Integer connectorId) {
        OcppTagActivityRecord record = ocppTagRepository.getRecord(idTag);
        if (record == null) {
            log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
            return new IdTagInfo().withStatus(AuthorizationStatus.INVALID);
        }

        if (isBlocked(record)) {
            log.error("The user with idTag '{}' is BLOCKED.", idTag);
            return new IdTagInfo()
                .withStatus(AuthorizationStatus.BLOCKED)
                .withParentIdTag(record.getParentIdTag())
                .withExpiryDate(getExpiryDateOrDefault(record));
        }

        if (isExpired(record, DateTime.now())) {
            log.error("The user with idTag '{}' is EXPIRED.", idTag);
            return new IdTagInfo()
                .withStatus(AuthorizationStatus.EXPIRED)
                .withParentIdTag(record.getParentIdTag())
                .withExpiryDate(getExpiryDateOrDefault(record));
        }

        // https://github.com/steve-community/steve/issues/219
        if (isStartTransactionReqContext && reachedLimitOfActiveTransactions(record)) {
            log.warn("The user with idTag '{}' is ALREADY in another transaction(s).", idTag);
            return new IdTagInfo()
                .withStatus(AuthorizationStatus.CONCURRENT_TX)
                .withParentIdTag(record.getParentIdTag())
                .withExpiryDate(getExpiryDateOrDefault(record));
        }

        log.debug("The user with idTag '{}' is ACCEPTED.", record.getIdTag());
        return new IdTagInfo()
            .withStatus(AuthorizationStatus.ACCEPTED)
            .withParentIdTag(record.getParentIdTag())
            .withExpiryDate(getExpiryDateOrDefault(record));
    }

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
}

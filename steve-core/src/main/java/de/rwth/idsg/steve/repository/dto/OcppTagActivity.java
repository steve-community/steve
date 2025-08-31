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
package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import ocpp.cp._2015._10.AuthorizationData;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toOffsetDateTime;

@Getter
@Builder
public class OcppTagActivity {
    private final int ocppTagPk;
    private final String idTag;
    private final @Nullable String parentIdTag;
    private final @Nullable Instant expiryDate;
    private final boolean inTransaction;
    private final boolean blocked;
    private final int maxActiveTransactionCount;
    private final long activeTransactionCount;
    private final String note;

    public boolean hasReachedLimitOfActiveTransactions() {
        // blocked
        if (maxActiveTransactionCount == 0) {
            return true;
        }

        // allow all
        if (maxActiveTransactionCount < 0) {
            return false;
        }

        // allow as specified
        return activeTransactionCount >= maxActiveTransactionCount;
    }

    public boolean isExpired(Instant date) {
        return expiryDate != null && date.isAfter(expiryDate);
    }

    public boolean isBlocked() {
        return blocked || maxActiveTransactionCount == 0;
    }

    /**
     * ConcurrentTx is only valid for StartTransactionRequest
     */
    public ocpp.cp._2015._10.AuthorizationStatus decideStatusForAuthData(Instant date) {
        if (isBlocked()) {
            return ocpp.cp._2015._10.AuthorizationStatus.BLOCKED;
        } else if (isExpired(date)) {
            return ocpp.cp._2015._10.AuthorizationStatus.EXPIRED;
            //        } else if (hasReachedLimitOfActiveTransactions()) {
            //            return ocpp.cp._2015._10.AuthorizationStatus.CONCURRENT_TX;
        } else {
            return ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED;
        }
    }

    public AuthorizationData mapToAuthorizationData(Instant date) {
        return new AuthorizationData()
                .withIdTag(idTag)
                .withIdTagInfo(new ocpp.cp._2015._10.IdTagInfo()
                        .withStatus(decideStatusForAuthData(date))
                        .withParentIdTag(parentIdTag)
                        .withExpiryDate(toOffsetDateTime(expiryDate)));
    }
}

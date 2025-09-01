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
package de.rwth.idsg.steve.jooq.mapper;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionStopEventActor;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Record;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;
import static jooq.steve.db.Tables.CHARGE_BOX;
import static jooq.steve.db.Tables.CONNECTOR;
import static jooq.steve.db.Tables.OCPP_TAG;
import static jooq.steve.db.Tables.TRANSACTION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionMapper {

    public static Transaction fromRecord(Record r) {
        return Transaction.builder()
                .id(r.get(TRANSACTION.TRANSACTION_PK))
                .chargeBoxId(r.get(CHARGE_BOX.CHARGE_BOX_ID))
                .connectorId(r.get(CONNECTOR.CONNECTOR_ID))
                .ocppIdTag(r.get(TRANSACTION.ID_TAG))
                .startTimestamp(toInstant(r.get(TRANSACTION.START_TIMESTAMP)))
                .startTimestampFormatted(DateTimeUtils.humanize(r.get(TRANSACTION.START_TIMESTAMP)))
                .startValue(r.get(TRANSACTION.START_VALUE))
                .stopTimestamp(toInstant(r.get(TRANSACTION.STOP_TIMESTAMP)))
                .stopTimestampFormatted(DateTimeUtils.humanize(r.get(TRANSACTION.STOP_TIMESTAMP)))
                .stopValue(r.get(TRANSACTION.STOP_VALUE))
                .stopReason(r.get(TRANSACTION.STOP_REASON))
                .chargeBoxPk(r.get(CHARGE_BOX.CHARGE_BOX_PK))
                .ocppTagPk(r.get(OCPP_TAG.OCPP_TAG_PK))
                .stopEventActor(toDto(r.get(TRANSACTION.STOP_EVENT_ACTOR)))
                .build();
    }

    private static TransactionStopEventActor toDto(jooq.steve.db.enums.TransactionStopEventActor actor) {
        if (actor == null) {
            return null;
        }
        return TransactionStopEventActor.valueOf(actor.name());
    }
}

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

import de.rwth.idsg.steve.repository.dto.OcppTagActivity;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OcppTagMapper {

    public static OcppTagActivity fromRecord(OcppTagActivityRecord r) {
        return OcppTagActivity.builder()
                .ocppTagPk(r.getOcppTagPk())
                .idTag(r.getIdTag())
                .expiryDate(toInstant(r.getExpiryDate()))
                .maxActiveTransactionCount(r.getMaxActiveTransactionCount())
                .note(r.getNote())
                .parentIdTag(r.getParentIdTag())
                .blocked(r.getBlocked())
                .inTransaction(r.getInTransaction())
                .activeTransactionCount(r.getActiveTransactionCount())
                .build();
    }
}

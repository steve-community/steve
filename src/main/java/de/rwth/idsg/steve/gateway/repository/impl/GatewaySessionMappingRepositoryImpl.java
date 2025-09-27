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
package de.rwth.idsg.steve.gateway.repository.impl;

import de.rwth.idsg.steve.gateway.repository.GatewaySessionMappingRepository;
import jooq.steve.db.enums.GatewaySessionMappingProtocol;
import jooq.steve.db.tables.records.GatewaySessionMappingRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static jooq.steve.db.Tables.GATEWAY_SESSION_MAPPING;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GatewaySessionMappingRepositoryImpl implements GatewaySessionMappingRepository {

    private final DSLContext ctx;

    @Override
    public Optional<GatewaySessionMappingRecord> findByTransactionPk(Integer transactionPk) {
        return ctx.selectFrom(GATEWAY_SESSION_MAPPING)
            .where(GATEWAY_SESSION_MAPPING.TRANSACTION_PK.eq(transactionPk))
            .fetchOptional();
    }

    @Override
    public Optional<GatewaySessionMappingRecord> findBySessionId(String sessionId) {
        return ctx.selectFrom(GATEWAY_SESSION_MAPPING)
            .where(GATEWAY_SESSION_MAPPING.SESSION_ID.eq(sessionId))
            .fetchOptional();
    }

    @Override
    public GatewaySessionMappingRecord createMapping(Integer transactionPk, GatewaySessionMappingProtocol protocol,
                                                      String sessionId, Integer partnerId) {
        GatewaySessionMappingRecord record = ctx.newRecord(GATEWAY_SESSION_MAPPING);
        record.setTransactionPk(transactionPk);
        record.setProtocol(protocol);
        record.setSessionId(sessionId);
        record.setPartnerId(partnerId);
        record.setCreatedAt(DateTime.now());
        record.store();
        return record;
    }
}
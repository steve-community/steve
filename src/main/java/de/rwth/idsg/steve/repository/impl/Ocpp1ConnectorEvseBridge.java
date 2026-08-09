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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import jooq.steve.db.Tables;
import jooq.steve.db.enums.EvseTopologySource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Evse.EVSE;
import static jooq.steve.db.tables.EvseConnector.EVSE_CONNECTOR;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.05.2026
 */
@Slf4j
public class Ocpp1ConnectorEvseBridge {

    public static SelectConditionStep<Record1<Integer>> evsePkSelect(DSLContext ctx, String chargeBoxId) {
        return evsePkSelect2(ctx, chargeBoxId, null);
    }

    public static SelectConditionStep<Record1<Integer>> evsePkSelect(DSLContext ctx, String chargeBoxId, @Nullable Integer connectorId) {
        Set<Integer> connectorIds = (connectorId == null) ? Collections.emptySet() : Set.of(connectorId);
        return evsePkSelect2(ctx, chargeBoxId, connectorIds);
    }

    public static SelectConditionStep<Record1<Integer>> evsePkSelect2(DSLContext ctx, String chargeBoxId, Set<Integer> connectorIds) {
        Condition evseIdCondition = CollectionUtils.isEmpty(connectorIds)
            ? DSL.trueCondition()
            : Tables.EVSE.EVSE_ID.in(connectorIds);

        return ctx.select(EVSE.EVSE_PK)
            .from(EVSE)
            .where(EVSE.CHARGE_BOX_ID.eq(chargeBoxId))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(evseIdCondition);
    }

    /**
     * If the connector information was not received before, insert it. Otherwise, ignore.
     *
     * @return evsePk
     */
    public static int insertIgnoreConnector(DSLContext ctx, String chargeBoxIdentity, int connectorId, boolean inTransactionAlready) {
        if (inTransactionAlready) {
            return insertIgnoreConnectorInternal(ctx, chargeBoxIdentity, connectorId);
        } else {
            return ctx.transactionResult(configuration ->
                insertIgnoreConnectorInternal(DSL.using(configuration), chargeBoxIdentity, connectorId)
            );
        }
    }

    private static int insertIgnoreConnectorInternal(DSLContext ctx, String chargeBoxIdentity, int connectorId) {
        var topology = ctx.select(EVSE.EVSE_PK, EVSE_CONNECTOR.EVSE_CONNECTOR_PK)
            .from(CHARGE_BOX)
            .leftJoin(EVSE)
                .on(EVSE.CHARGE_BOX_ID.eq(CHARGE_BOX.CHARGE_BOX_ID))
                .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
                .and(EVSE.EVSE_ID.eq(connectorId))
            .leftJoin(EVSE_CONNECTOR)
                .on(EVSE_CONNECTOR.EVSE_PK.eq(EVSE.EVSE_PK))
                .and(EVSE_CONNECTOR.CONNECTOR_ID.eq(1))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxIdentity))
            .forUpdate()
            .fetchOne();

        if (topology == null) {
            throw new SteveException("Charge box with id '%s' not found", chargeBoxIdentity); // should not happen
        }

        Integer evsePk = topology.value1();
        Integer evseConnectorPk = topology.value2();

        if (evsePk == null) {
            evsePk = ctx.insertInto(EVSE)
                .set(EVSE.CHARGE_BOX_ID, chargeBoxIdentity)
                .set(EVSE.TOPOLOGY_SOURCE, EvseTopologySource.ocpp1)
                .set(EVSE.EVSE_ID, connectorId)
                .returning(EVSE.EVSE_PK)
                .fetchOne(EVSE.EVSE_PK);

            log.debug("The connector {}/{} is NEW, and inserted into DB.", chargeBoxIdentity, connectorId);
        }

        if (connectorId != 0 && evseConnectorPk == null) {
            final int defaultEvseConnectorId = 1;

            ctx.insertInto(EVSE_CONNECTOR)
                .set(EVSE_CONNECTOR.EVSE_PK, evsePk)
                .set(EVSE_CONNECTOR.CONNECTOR_ID, defaultEvseConnectorId)
                .execute();

            log.debug("The evse_connector {}/{}/{} is NEW, and inserted into DB.", chargeBoxIdentity, connectorId, defaultEvseConnectorId);
        }

        return evsePk;
    }
}

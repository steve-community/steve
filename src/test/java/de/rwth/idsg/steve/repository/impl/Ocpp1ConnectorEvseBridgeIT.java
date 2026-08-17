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

import jooq.steve.db.enums.EvseTopologySource;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Evse.EVSE;
import static jooq.steve.db.tables.EvseConnector.EVSE_CONNECTOR;

public class Ocpp1ConnectorEvseBridgeIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertIgnoreConnectorCreatesUnrelatedTopologiesConcurrently() throws Exception {
        int chargeBoxCount = 6;
        String prefix = uniqueId("concurrent_topology");
        List<String> chargeBoxIds = new ArrayList<>(chargeBoxCount);

        for (int i = 0; i < chargeBoxCount; i++) {
            String chargeBoxId = prefix + "_" + i;
            chargeBoxIds.add(chargeBoxId);
            dslContext.insertInto(CHARGE_BOX)
                .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
                .execute();
        }

        List<Integer> evsePks = runConcurrently(chargeBoxIds.stream()
            .<Supplier<Integer>>map(chargeBoxId -> () -> Ocpp1ConnectorEvseBridge.insertIgnoreConnector(
                dslContext, chargeBoxId, 0, false
            ))
            .toList());

        int evseCount = dslContext.fetchCount(
            EVSE,
            EVSE.CHARGE_BOX_ID.in(chargeBoxIds)
                .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
                .and(EVSE.EVSE_ID.eq(0))
        );

        Assertions.assertEquals(chargeBoxCount, evseCount);
        Assertions.assertEquals(chargeBoxCount, evsePks.stream().distinct().count());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertIgnoreConnectorCreatesSameTopologyIdempotently() throws Exception {
        int requestCount = 3;
        int connectorId = 2;
        String chargeBoxId = uniqueId("concurrent_same_topology");

        dslContext.insertInto(CHARGE_BOX)
            .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
            .execute();

        List<Integer> evsePks = runConcurrently(IntStream.range(0, requestCount)
            .<Supplier<Integer>>mapToObj(ignored -> () -> Ocpp1ConnectorEvseBridge.insertIgnoreConnector(
                dslContext, chargeBoxId, connectorId, false
            ))
            .toList());

        Integer canonicalEvsePk = dslContext.select(EVSE.EVSE_PK)
            .from(EVSE)
            .where(EVSE.CHARGE_BOX_ID.eq(chargeBoxId))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(EVSE.EVSE_ID.eq(connectorId))
            .fetchOne(EVSE.EVSE_PK);

        Integer physicalConnectorCount = dslContext.selectCount()
            .from(EVSE_CONNECTOR)
            .join(EVSE).on(EVSE.EVSE_PK.eq(EVSE_CONNECTOR.EVSE_PK))
            .where(EVSE.CHARGE_BOX_ID.eq(chargeBoxId))
            .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
            .and(EVSE.EVSE_ID.eq(connectorId))
            .and(EVSE_CONNECTOR.CONNECTOR_ID.eq(1))
            .fetchOne(0, Integer.class);

        Assertions.assertNotNull(canonicalEvsePk);
        Assertions.assertTrue(evsePks.stream().allMatch(canonicalEvsePk::equals));
        Assertions.assertEquals(1, physicalConnectorCount);
    }

    private static <T> List<T> runConcurrently(List<Supplier<T>> operations) throws Exception {
        var ready = new CountDownLatch(operations.size());
        var start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(operations.size());
        List<Future<T>> futures = new ArrayList<>(operations.size());

        try {
            for (Supplier<T> operation : operations) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return operation.get();
                }));
            }

            Assertions.assertTrue(ready.await(10, TimeUnit.SECONDS), "Workers did not become ready in time");
            start.countDown();

            List<T> results = new ArrayList<>(operations.size());
            for (Future<T> future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }
}

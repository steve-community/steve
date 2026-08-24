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
package de.rwth.idsg.steve.issues;

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.Helpers;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import jooq.steve.db.enums.EvseTopologySource;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Evse.EVSE;

/**
 * Issue 2111: the other half of the race #2107 was about. There, several DISTINCT charge
 * boxes reported their first connector status at once and deadlocked while each inserted a
 * row of its own. Here the burst comes from ONE charge box reporting ONE connector, so the
 * second transaction to arrive finds the unique key already taken and takes the
 * {@code ON DUPLICATE KEY UPDATE} branch of
 * {@code Ocpp1ConnectorEvseBridge.insertIgnoreConnectorInternal}.
 *
 * <p>On MariaDB that branch raises {@code Record has changed since last read in table 'evse'}
 * (error 1020, snapshot isolation — MDEV-37208), and it escapes to the charge point as a
 * {@code CALLERROR}, exactly as the 1213 deadlock did before #2108. The obligation is the
 * same one OCPP 1.6 states: {@code StatusNotification.req} is answered with
 * {@code StatusNotification.conf}.
 *
 * <p>Two sessions of one charge box is not a contrivance: SteVe accepts several
 * ({@code ws.session.select.strategy} exists for exactly that), and a station that reconnects
 * before the old socket is reaped is the ordinary way to get there.
 *
 * <p>THIS TEST IS EXPECTED TO FAIL until #2111 is fixed. It is the wire-level statement of
 * what {@code Ocpp1ConnectorEvseBridgeIT.insertIgnoreConnectorCreatesSameTopologyIdempotently}
 * says at the repository level.
 *
 * @see <a href="https://github.com/steve-community/steve/issues/2111">Issue 2111</a>
 * @see <a href="https://jira.mariadb.org/browse/MDEV-37208">MDEV-37208</a>
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Issue2111IT {

    /** Sessions of the one charge box, all reporting the one connector. */
    private static final int SESSION_COUNT = 3;

    /** Non-zero, so the physical connector row is exercised alongside the EVSE row. */
    private static final int CONNECTOR_ID = 1;

    private static final int ROUNDS = 3;

    private static final long BARRIER_TIMEOUT_SECONDS = 60;
    private static final long RESULT_TIMEOUT_SECONDS = 60;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ServerProperties serverProperties;

    /**
     * The port the server actually bound.
     *
     * <p>Not {@code DEFINED_PORT}: Spring caches one context per configuration and closes
     * none of them between classes, so a second fixed-port context on 8080 — and this one's
     * cache key differs from {@code Ocpp16JsonCsmsCertificationIT}'s, which sets an
     * {@code octt-quirks} property — means whichever class runs second dies with
     * {@code BindException: Address already in use}. Measured.
     */
    @LocalServerPort
    private int port;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup() {
        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void concurrentFirstStatusNotificationOfOneStationIsAnsweredAndRecorded() throws Exception {
        for (int round = 1; round <= ROUNDS; round++) {
            log.info("----- burst {}/{} -----", round, ROUNDS);
            String chargeBoxId = registerStation();

            burstOneStation(chargeBoxId);

            int recorded = dslContext.fetchCount(
                EVSE,
                EVSE.CHARGE_BOX_ID.eq(chargeBoxId)
                    .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
                    .and(EVSE.EVSE_ID.eq(CONNECTOR_ID))
            );

            // Exactly one: the point of insertIgnoreConnector is that concurrent reports of
            // the same connector converge on one row rather than failing or duplicating it.
            Assertions.assertEquals(1, recorded,
                "burst " + round + ": the connector must be recorded exactly once");
        }
    }

    private void burstOneStation(String chargeBoxId) throws Exception {
        String path = jsonPath();

        CountDownLatch ready = new CountDownLatch(SESSION_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(SESSION_COUNT);
        List<Future<StatusNotificationResponse>> futures = new ArrayList<>(SESSION_COUNT);
        boolean terminated;

        try {
            for (int session = 0; session < SESSION_COUNT; session++) {
                futures.add(executor.submit(session(chargeBoxId, path, ready, start)));
            }

            Assertions.assertTrue(ready.await(BARRIER_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "sessions did not finish booting in time");
            start.countDown();

            List<String> failures = new ArrayList<>();
            for (int i = 0; i < futures.size(); i++) {
                int session = i;
                try {
                    Assertions.assertNotNull(futures.get(i).get(RESULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                } catch (ExecutionException e) {
                    // A CALLERROR surfaces here: OcppJsonChargePoint refuses to hand back a
                    // response of the wrong message type.
                    failures.add("session " + session + " -> " + e.getCause());
                } catch (TimeoutException e) {
                    // Collected rather than thrown: the summary below is this test's entire
                    // output, and a session that was never answered at all is a different
                    // failure from one answered with a CALLERROR. Throwing here would report
                    // the first slow session and say nothing about the other two.
                    futures.get(i).cancel(true);
                    failures.add("session " + session + " -> no answer within "
                        + RESULT_TIMEOUT_SECONDS + "s");
                }
            }

            Assertions.assertTrue(failures.isEmpty(),
                () -> failures.size() + "/" + SESSION_COUNT
                    + " sessions were not answered with a StatusNotification.conf: " + failures);
        } finally {
            start.countDown();
            executor.shutdownNow();
            // Recorded here, asserted below: an exception thrown from a finally block
            // REPLACES the one already in flight, and the one in flight is the summary this
            // test exists to produce.
            terminated = executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        Assertions.assertTrue(terminated, "sessions did not terminate in time");
    }

    /**
     * One session of the charge box.
     *
     * <p>The charge point is built inside the worker on purpose — {@link OcppJsonChargePoint}
     * remembers the thread that created it and interrupts that thread on a protocol error, so
     * an instance built on the main thread would report another session's failure.
     */
    private static Callable<StatusNotificationResponse> session(String chargeBoxId,
                                                                String path,
                                                                CountDownLatch ready,
                                                                CountDownLatch start) {
        return () -> {
            OcppJsonChargePoint chargePoint = null;
            try {
                try {
                    chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, chargeBoxId, path).start();

                    BootNotificationResponse boot = chargePoint.send(
                        new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                        BootNotificationResponse.class
                    );
                    Assertions.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
                } finally {
                    ready.countDown();
                }

                if (!start.await(BARRIER_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("start signal was not received in time");
                }

                return chargePoint.send(
                    new StatusNotificationRequest()
                        .withConnectorId(CONNECTOR_ID)
                        .withStatus(ChargePointStatus.AVAILABLE)
                        .withErrorCode(ChargePointErrorCode.NO_ERROR)
                        .withTimestamp(DateTime.now()),
                    StatusNotificationResponse.class
                );
            } finally {
                if (chargePoint != null) {
                    chargePoint.close();
                }
            }
        };
    }

    /**
     * Same shape as {@link Helpers#getJsonPath(ServerProperties)}, with the port the server
     * bound rather than the one it was configured with — the latter is 0 under
     * {@code RANDOM_PORT}. Leading {@code ws:/} and not {@code ws://} for the same reason as
     * there: {@code getAddress()} already renders with a slash.
     */
    private String jsonPath() {
        return "ws:/"
            + serverProperties.getAddress()
            + ":"
            + port
            + serverProperties.getServlet().getContextPath()
            + WebSocketConfiguration.PATH_INFIX;
    }

    /** A charge box with a row and nothing else: no {@code evse} row yet, which is what makes
     *  the burst below a race between three FIRST reports of the same connector. */
    private String registerStation() {
        String chargeBoxId = "issue2111_" + UUID.randomUUID().toString().replace("-", "");
        dslContext.insertInto(CHARGE_BOX)
            .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
            .execute();
        return chargeBoxId;
    }
}

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
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.test.web.server.LocalServerPort;
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
 * Issue 2107: several charge points sending their FIRST {@code StatusNotification(connectorId=0)}
 * at the same time deadlocked on {@code insert into evse} (MariaDB 1213), and the deadlock was
 * returned to the charge point as an OCPP {@code CALLERROR} instead of a
 * {@code StatusNotification.conf}. The connector status was silently not recorded either.
 *
 * <p>This test is deliberately at the PROTOCOL level, over real WebSocket connections, where
 * {@code Ocpp1ConnectorEvseBridgeIT} covers the repository call directly. What made 2107 a bug
 * worth fixing is not that a repository method threw, it is that OCPP 1.6 obliges the Central
 * System to answer {@code StatusNotification.req} with {@code StatusNotification.conf} — and
 * that obligation is only observable from the wire.
 *
 * <p>Two properties of the setup carry the whole reproduction, and neither is incidental:
 * <ul>
 *   <li><b>Fresh charge boxes.</b> Once a charge box's {@code evse} rows exist, later
 *       StatusNotifications do not insert and there is nothing left to race on. Every round
 *       therefore registers charge boxes that have never sent anything.</li>
 *   <li><b>One barrier, one message.</b> The stations connect and boot first, then wait on a
 *       shared latch, so the only thing that happens simultaneously is the StatusNotification
 *       itself.</li>
 * </ul>
 *
 * <p>It is a sampler: the failure it targets was probabilistic (roughly half of the bursts of
 * three, before the fix). Hence several rounds, and an assertion that can only go red when the
 * regression is back — a station answered with a CALLERROR, or a connector status that was
 * never written.
 *
 * @see <a href="https://github.com/steve-community/steve/issues/2107">Issue 2107</a>
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Issue2107IT {

    /**
     * Stations per burst. Six matches {@code Ocpp1ConnectorEvseBridgeIT}, and a wider burst
     * reproduced more often than a narrow one when this was measured against the broken build.
     */
    private static final int STATION_COUNT = 6;

    /** Bursts. Each one is an independent draw, on charge boxes the CSMS has never seen. */
    private static final int ROUNDS = 3;

    /** The connector id a charge point reports itself with, and the one 2107 was raised on. */
    private static final int CONNECTOR_ID = 0;

    private static final long BARRIER_TIMEOUT_SECONDS = 60;
    private static final long RESULT_TIMEOUT_SECONDS = 60;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ServerProperties serverProperties;

    /**
     * The port the server actually bound, which is what {@code RANDOM_PORT} is for here.
     *
     * <p>A fixed port would be the shorter annotation, and it would break the build: Spring
     * caches one context per configuration and does not close them between classes, so a
     * second {@code DEFINED_PORT} context whose key differs from
     * {@code Ocpp16JsonCsmsCertificationIT}'s — and this one's does, it sets no
     * {@code octt-quirks} property — starts while the first is still listening on 8080.
     * Measured: whichever of the two runs second dies with
     * {@code BindException: Address already in use}. The TLS certification IT gets away with
     * a fixed port only because {@code application-test-tls.yml} moves it to 8443.
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
    public void concurrentFirstStatusNotificationIsAnsweredAndRecorded() throws Exception {
        for (int round = 1; round <= ROUNDS; round++) {
            log.info("----- burst {}/{} -----", round, ROUNDS);
            List<String> chargeBoxIds = registerStations();

            burstStatusNotification(chargeBoxIds);

            int recorded = dslContext.fetchCount(
                EVSE,
                EVSE.CHARGE_BOX_ID.in(chargeBoxIds)
                    .and(EVSE.TOPOLOGY_SOURCE.eq(EvseTopologySource.ocpp1))
                    .and(EVSE.EVSE_ID.eq(CONNECTOR_ID))
            );

            // The half a charge point cannot see: before the fix, a station whose insert lost
            // the race got its CALLERROR *and* no row at all.
            Assertions.assertEquals(STATION_COUNT, recorded,
                "burst " + round + ": every station's connector status must be recorded");
        }
    }

    /**
     * Every station sends its StatusNotification in the same instant. Each one is answered with
     * a {@code StatusNotification.conf} or the test fails naming the stations that were not.
     */
    private void burstStatusNotification(List<String> chargeBoxIds) throws Exception {
        String path = jsonPath();

        CountDownLatch ready = new CountDownLatch(chargeBoxIds.size());
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(chargeBoxIds.size());
        List<Future<StatusNotificationResponse>> futures = new ArrayList<>(chargeBoxIds.size());
        boolean terminated;

        try {
            for (String chargeBoxId : chargeBoxIds) {
                futures.add(executor.submit(station(chargeBoxId, path, ready, start)));
            }

            Assertions.assertTrue(ready.await(BARRIER_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "stations did not finish booting in time");
            start.countDown();

            List<String> failures = new ArrayList<>();
            for (int i = 0; i < futures.size(); i++) {
                String chargeBoxId = chargeBoxIds.get(i);
                try {
                    Assertions.assertNotNull(futures.get(i).get(RESULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                } catch (ExecutionException e) {
                    // A CALLERROR surfaces here: OcppJsonChargePoint refuses to hand back a
                    // response of the wrong message type. The cause of the CALLERROR itself is
                    // in the server log of this run.
                    failures.add(chargeBoxId + " -> " + e.getCause());
                } catch (TimeoutException e) {
                    // Collected rather than thrown: the summary below is this test's entire
                    // output, and a station that was never answered at all is a different
                    // failure from one answered with a CALLERROR.
                    futures.get(i).cancel(true);
                    failures.add(chargeBoxId + " -> no answer within "
                        + RESULT_TIMEOUT_SECONDS + "s");
                }
            }

            Assertions.assertTrue(failures.isEmpty(),
                () -> failures.size() + "/" + chargeBoxIds.size()
                    + " stations were not answered with a StatusNotification.conf: " + failures);
        } finally {
            start.countDown();
            executor.shutdownNow();
            // Recorded here, asserted below: an exception thrown from a finally block
            // REPLACES the one already in flight, and the one in flight is the summary this
            // test exists to produce.
            terminated = executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        Assertions.assertTrue(terminated, "stations did not terminate in time");
    }

    /**
     * One station: connect, boot, wait for the others, then the one message under test.
     *
     * <p>The charge point is built inside the worker on purpose — {@link OcppJsonChargePoint}
     * remembers the thread that created it and interrupts that thread on a protocol error, so
     * an instance built on the main thread would report another station's failure.
     */
    private static Callable<StatusNotificationResponse> station(String chargeBoxId,
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
                    // Whether the setup worked or not. A station that never booted would
                    // otherwise cost the coordinator the whole barrier timeout and report
                    // "did not finish booting in time" instead of the failure already sitting
                    // in this station's future.
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

    /**
     * Charge boxes with a row and nothing else: registered enough to be allowed to connect,
     * with no {@code evse} row yet. That is the state the race needs.
     */
    private List<String> registerStations() {
        String prefix = "issue2107_" + UUID.randomUUID().toString().replace("-", "");
        List<String> chargeBoxIds = new ArrayList<>(STATION_COUNT);

        for (int i = 0; i < STATION_COUNT; i++) {
            String chargeBoxId = prefix + "_" + i;
            chargeBoxIds.add(chargeBoxId);
            dslContext.insertInto(CHARGE_BOX)
                .set(CHARGE_BOX.CHARGE_BOX_ID, chargeBoxId)
                .execute();
        }
        return chargeBoxIds;
    }
}

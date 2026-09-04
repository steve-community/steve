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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;

/**
 * Issue 3: the server-side WebSocket keep-alive used to be a hardcoded 15 minutes, which is longer than
 * the idle timeout of most managed reverse proxies. It is now {@code steve.ocpp.ws-ping-interval}.
 *
 * <p>This is the wire-level statement of what the property is for: a station that sends no OCPP message
 * at all still exchanges ping/pong, and only a ping can move its last heartbeat here — nothing else in
 * this test talks to the server after the upgrade.
 *
 * @see <a href="https://github.com/steve-community/steve/issues/3">Issue 3</a>
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "steve.ocpp.ws-ping-interval=1s"
)
public class Issue3IT {

    private static final long TIMEOUT_IN_MILLIS = 30_000;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ServerProperties serverProperties;

    @LocalServerPort
    private int port;

    private String chargeBoxId;

    @AfterEach
    public void cleanUp() {
        if (chargeBoxId != null) {
            dslContext.deleteFrom(CHARGE_BOX).where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxId)).execute();
        }
    }

    @Test
    public void idleSessionIsPinged() throws Exception {
        chargeBoxId = registerStation();

        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, chargeBoxId, jsonPath()).start();
        try {
            Assertions.assertNull(lastHeartbeat(chargeBoxId), "precondition: the station never reported yet");

            DateTime heartbeat = awaitHeartbeat(chargeBoxId);

            Assertions.assertNotNull(
                heartbeat,
                "an idle session should have been pinged, and the pong should have moved the heartbeat"
            );
        } finally {
            chargePoint.close();
        }
    }

    /**
     * The pong is handled asynchronously, so we poll instead of sleeping for a fixed multiple of the
     * interval.
     */
    private DateTime awaitHeartbeat(String chargeBoxId) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TIMEOUT_IN_MILLIS;

        while (System.currentTimeMillis() < deadline) {
            DateTime heartbeat = lastHeartbeat(chargeBoxId);
            if (heartbeat != null) {
                return heartbeat;
            }
            Thread.sleep(250);
        }
        return null;
    }

    private DateTime lastHeartbeat(String chargeBoxId) {
        return dslContext.select(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxId))
            .fetchOne(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP);
    }

    private String registerStation() {
        String id = "issue3_" + UUID.randomUUID().toString().replace("-", "");
        dslContext.insertInto(CHARGE_BOX)
            .set(CHARGE_BOX.CHARGE_BOX_ID, id)
            .execute();
        return id;
    }

    /**
     * Same shape as {@link Helpers#getJsonPath(ServerProperties)}, with the port the server bound rather
     * than the one it was configured with — the latter is 0 under {@code RANDOM_PORT}.
     */
    private String jsonPath() {
        return "ws:/"
            + serverProperties.getAddress()
            + ":"
            + port
            + serverProperties.getServlet().getContextPath()
            + WebSocketConfiguration.PATH_INFIX;
    }
}

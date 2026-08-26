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
package de.rwth.idsg.steve.ocpp.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketPingServiceTest {

    private static final Duration PING_INTERVAL = Duration.ofMinutes(15);

    private final TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
    private final ScheduledFuture<?> schedule = Mockito.mock(ScheduledFuture.class);

    @BeforeEach
    public void stubScheduler() {
        doReturn(schedule).when(taskScheduler).scheduleAtFixedRate(any(), any(Instant.class), any(Duration.class));
    }

    @Test
    public void testSessionIsPingedAtTheConfiguredInterval() throws Exception {
        var service = newService(PING_INTERVAL);
        var session = getMockSession();

        service.register("foo", session);
        scheduledPing().run();

        verify(session).sendMessage(any(PingMessage.class));
    }

    /**
     * If the session is not open anymore, the connection went away without proper closing steps and we
     * have a dangling reference. See {@link PingTask}.
     */
    @Test
    public void testDanglingSessionIsClosed() throws Exception {
        var service = newService(PING_INTERVAL);
        var session = getMockSession();
        when(session.isOpen()).thenReturn(false);

        service.register("foo", session);
        scheduledPing().run();

        verify(session).close();
        verify(session, never()).sendMessage(any());
    }

    @Test
    public void testDeregisterCancelsTheSchedule() {
        var service = newService(PING_INTERVAL);
        var session = getMockSession();

        service.register("foo", session);
        service.deregister(session);

        verify(schedule).cancel(true);
    }

    @Test
    public void testDeregisterOfUnknownSessionIsHarmless() {
        var service = newService(PING_INTERVAL);

        service.deregister(getMockSession());

        verify(schedule, never()).cancel(true);
    }

    @Test
    public void testZeroIntervalDisablesPings() {
        var service = newService(Duration.ZERO);

        service.register("foo", getMockSession());

        verify(taskScheduler, never()).scheduleAtFixedRate(any(), any(Instant.class), any(Duration.class));
    }

    @Test
    public void testRejectsNegativeInterval() {
        assertThrows(IllegalArgumentException.class, () -> newService(Duration.ofMinutes(-1)));
    }

    @Test
    public void testRejectsSubSecondInterval() {
        assertThrows(IllegalArgumentException.class, () -> newService(Duration.ofMillis(500)));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private WebSocketPingService newService(Duration pingInterval) {
        return new WebSocketPingService(pingInterval, taskScheduler);
    }

    private Runnable scheduledPing() {
        var captor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskScheduler).scheduleAtFixedRate(captor.capture(), any(Instant.class), eq(PING_INTERVAL));
        return captor.getValue();
    }

    private static JettyWebSocketSession getMockSession() {
        JettyWebSocketSession session = Mockito.mock(JettyWebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getId()).thenReturn(UUID.randomUUID().toString());
        return session;
    }
}

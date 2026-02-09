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

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.support.NoOpTaskScheduler;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;

import java.util.UUID;

import static org.mockito.Mockito.when;

public class SessionContextStoreTest {

    @Test
    public void testAdd() {
        var store = new SessionContextStoreImpl(
            WsSessionSelectStrategyEnum.ALWAYS_LAST,
            new NoOpTaskScheduler(),
            new FutureResponseContextStoreImpl()
        );

        int sizeBeforeAdd = store.getSize("foo");
        Assertions.assertEquals(0, sizeBeforeAdd);

        // add first
        {
            store.add("foo", getMockSession());
            int sizeAfterAdd =  store.getSize("foo");
            Assertions.assertEquals(1, sizeAfterAdd);
        }

        // add second
        {
            store.add("foo", getMockSession());
            int sizeAfterAdd =  store.getSize("foo");
            Assertions.assertEquals(2, sizeAfterAdd);
        }
    }

    @Test
    public void testRemove() {
        var store = new SessionContextStoreImpl(
            WsSessionSelectStrategyEnum.ALWAYS_LAST,
            new NoOpTaskScheduler(),
            new FutureResponseContextStoreImpl()
        );

        int sizeBeforeAdd = store.getSize("foo");
        Assertions.assertEquals(0, sizeBeforeAdd);

        var session1 = getMockSession();
        var session2 = getMockSession();

        // prepare with 2 sessions
        {
            store.add("foo", session1);
            store.add("foo", session2);
            int sizeAfterAdd = store.getSize("foo");

            Assertions.assertEquals(2, sizeAfterAdd);
            Assertions.assertEquals(2, store.getSize("foo"));
        }

        // remove first
        {
            store.remove("foo", session1);
            int sizeAfterRemove = store.getSize("foo");
            Assertions.assertEquals(1, sizeAfterRemove);
        }

        // remove second
        {
            store.remove("foo", session2);
            int sizeAfterRemove = store.getSize("foo");
            Assertions.assertEquals(0, sizeAfterRemove);
        }

        // try removing another non-existing
        {
            store.remove("foo", getMockSession());
            int sizeAfterRemove = store.getSize("foo");
            Assertions.assertEquals(0, sizeAfterRemove);
            Assertions.assertEquals(0, store.getSize("foo"));
        }
    }

    private static JettyWebSocketSession getMockSession() {
        JettyWebSocketSession session = Mockito.mock(JettyWebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getId()).thenReturn(UUID.randomUUID().toString());
        return session;
    }
}

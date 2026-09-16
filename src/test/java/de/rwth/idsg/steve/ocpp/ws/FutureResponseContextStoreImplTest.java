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

import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FutureResponseContextStoreImplTest {

    @Test
    public void pollRemovesEmptySessionStore() {
        var store = new FutureResponseContextStoreImpl();
        var context = mock(FutureResponseContext.class);

        store.add("session", "message", context);

        assertSame(context, store.poll("session", "message"));
        assertFalse(store.containsKey("session"));
    }

    @Test
    public void pollKeepsSessionStoreWhileItContainsOtherEntries() {
        var store = new FutureResponseContextStoreImpl();
        var firstContext = mock(FutureResponseContext.class);
        var secondContext = mock(FutureResponseContext.class);

        store.add("session", "first", firstContext);
        store.add("session", "second", secondContext);

        assertSame(firstContext, store.poll("session", "first"));
        assertTrue(store.containsKey("session"));
        assertSame(secondContext, store.poll("session", "second"));
        assertFalse(store.containsKey("session"));
    }

    @Test
    public void addRetainsContextUntilRetentionPeriodIsExceeded() {
        var store = new FutureResponseContextStoreImpl();
        var retainedContext = mock(FutureResponseContext.class);
        var newContext = mock(FutureResponseContext.class);

        when(retainedContext.hasTimedOut(any())).thenReturn(true);
        when(retainedContext.hasExceededRetentionPeriod(any())).thenReturn(false);

        store.add("session", "retained", retainedContext);
        store.add("session", "new", newContext);

        assertSame(retainedContext, store.poll("session", "retained"));
    }

    @Test
    public void addEvictsContextAfterRetentionPeriodIsExceeded() {
        var store = new FutureResponseContextStoreImpl();
        var expiredContext = mock(FutureResponseContext.class);
        var newContext = mock(FutureResponseContext.class);

        when(expiredContext.hasExceededRetentionPeriod(any())).thenReturn(true);

        store.add("session", "expired", expiredContext);
        store.add("session", "new", newContext);

        assertNull(store.poll("session", "expired"));
        assertSame(newContext, store.poll("session", "new"));
    }
}

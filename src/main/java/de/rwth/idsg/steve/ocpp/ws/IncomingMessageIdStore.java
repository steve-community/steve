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

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import de.rwth.idsg.steve.SteveException;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Why hashes instead of raw messageId strings? A {@code Set<String>} can grow quickly for long-lived sessions and has
 * significant per-entry overhead.
 *
 * <p>Implementation detail:
 * we hash with Guava {@code murmur3_128()}, but store only {@code asLong()} in memory.
 * So the stored key space is 64-bit per message id.
 * This keeps memory usage low while preserving very low collision probability for practical session sizes.</p>
 *
 * <p> Rough order-of-magnitude memory comparison per entry (current implementation):
 * {@code Set<String>} ~120 B,
 * boxed {@code Set<Long>} ~64 B,
 * {@code LongOpenHashSet} (64-bit fingerprints) ~10-16 B.
 * This is roughly 85-92% less than {@code Set<String>}, and about 75-85% less than boxed {@code Set<Long>}.
 *
 * <p> Thread-safety note:
 * {@code LongOpenHashSet} is not thread-safe by itself and normally needs external synchronization
 * in concurrent contexts. In this code path, direct synchronization on the set is not required,
 * because calls are serialized by the outer per-chargeBox {@code Striped<Lock>} in
 * {@link SessionContextStoreImpl}.
 *
 * <p> Capacity boundary:
 * this store can enforce an optional per-session upper bound for unique incoming CALL messageIds.
 * When the limit is exceeded, it throws an exception. This is intentional and causes the WebSocket session to be
 * closed by the framework error handling path, avoiding unbounded growth for long-lived or misbehaving sessions.
 * Regular charging stations should reconnect which will create a fresh session, effectively resetting duplicate-call-id
 * tracking.
 *
 * <p> Collision risk (birthday approximation, 64-bit stored key space):
 * p ~= n(n-1)/(2*2^64), where n is the number of messageIds in one session.
 * This is very small for practical values:
 * n=10k -> ~2.7e-10%,
 * n=100k -> ~2.7e-8%,
 * n=1M -> ~2.7e-6%.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.02.2026
 */
public class IncomingMessageIdStore {

    private static final HashFunction MURMUR3_128 = Hashing.murmur3_128();

    private final ConcurrentHashMap<String, LongOpenHashSet> lookupBySessionId = new ConcurrentHashMap<>();
    private final Integer maxTrackedMessageIdsPerSession;

    /**
     * @param maxTrackedMessageIdsPerSession positive value enables bounded mode; {@code null} or non-positive value
     *                                       disables the cap (unbounded mode).
     */
    IncomingMessageIdStore(Integer maxTrackedMessageIdsPerSession) {
        boolean disable = (maxTrackedMessageIdsPerSession == null || maxTrackedMessageIdsPerSession <= 0);
        this.maxTrackedMessageIdsPerSession = disable ? null : maxTrackedMessageIdsPerSession;
    }

    void addSession(WebSocketSession session) {
        lookupBySessionId.computeIfAbsent(session.getId(), innerSession -> new LongOpenHashSet());
    }

    void removeSession(WebSocketSession session) {
        lookupBySessionId.remove(session.getId());
    }

    Boolean registerIncomingCallId(WebSocketSession session, @NotNull String messageId) {
        LongOpenHashSet set = lookupBySessionId.get(session.getId());
        if (set == null) {
            // return null in order to stop processing this message. if the set is null, we clearly don't know this
            // session. it might imply some add/remove race or some other unexpected edge case.
            return null;
        }

        long fingerprint = MURMUR3_128.hashString(messageId, StandardCharsets.UTF_8).asLong();
        boolean success = set.add(fingerprint);
        if (!success) {
            return false;
        }

        if (maxTrackedMessageIdsPerSession != null && set.size() > maxTrackedMessageIdsPerSession) {
            throw new SteveException(
                "Incoming CALL messageId limit of %s exceeded for session '%s'",
                maxTrackedMessageIdsPerSession, session.getId()
            );
        }
        return true;
    }
}

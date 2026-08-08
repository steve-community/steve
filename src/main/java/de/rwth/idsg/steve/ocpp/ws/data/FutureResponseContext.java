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
package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2015
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FutureResponseContext {

    /**
     * Internal safety window for matching inbound CALL_RESULT/CALL_ERROR messages.
     * This limits how long a request context is considered valid for correlation.
     */
    public static final int TIMEOUT_IN_SECONDS = 30;

    private final CommunicationTask task;
    private final Class<? extends ResponseType> responseClass;

    /**
     * Timestamp used to detect stale response contexts and prevent late responses
     * from being correlated as if they were still active.
     */
    private final Instant createdAt = Instant.now();

    public boolean hasTimedOut(Instant now) {
        return createdAt.plusSeconds(TIMEOUT_IN_SECONDS).isBefore(now);
    }
}

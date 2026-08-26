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

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Keeps the WebSocket connections of the JSON charge points alive, such that neither the station nor an
 * intermediary (proxy, load balancer, NAT gateway) closes a connection that is idle on the OCPP level.
 *
 * Owning the schedules here keeps {@link SessionContextStoreImpl} out of the scheduling business, and
 * gives the disabled case somewhere to live other than a null {@code ScheduledFuture} carried around by
 * every session context.
 */
@Slf4j
@Component
public class WebSocketPingService {

    /**
     * Key   (String)          = WebSocket session id
     * Value (ScheduledFuture) = the periodic ping of that session
     */
    private final ConcurrentHashMap<String, ScheduledFuture<?>> schedules = new ConcurrentHashMap<>();

    private final Duration pingInterval;
    private final TaskScheduler taskScheduler;

    /**
     * Required despite the single-constructor rule: the seam below is a second constructor, so Spring has
     * to be told which one to call.
     */
    @Autowired
    public WebSocketPingService(TaskScheduler taskScheduler) {
        this(WebSocketConfiguration.PING_INTERVAL, taskScheduler);
    }

    WebSocketPingService(Duration pingInterval, TaskScheduler taskScheduler) {
        this.pingInterval = pingInterval;
        this.taskScheduler = taskScheduler;

        log.info("Pinging WebSocket sessions every {}", pingInterval);
    }

    public void register(String chargeBoxId, WebSocketSession session) {
        ScheduledFuture<?> schedule = taskScheduler.scheduleAtFixedRate(
            new PingTask(chargeBoxId, session),
            Instant.now().plus(pingInterval),
            pingInterval
        );

        schedules.put(session.getId(), schedule);
    }

    public void deregister(WebSocketSession session) {
        ScheduledFuture<?> schedule = schedules.remove(session.getId());
        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}

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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.dto.ChargeBoxActivity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.UnaryOperator;

/**
 * Keeps the time of the last OCPP message and of the last WebSocket pong of each station in memory, and saves
 * them to the database periodically and on shutdown. This way, the database load does not depend on how often
 * stations send messages or answer pings.
 *
 * <p>The saved timestamps can therefore lag by up to {@code steve.ocpp.activity-save-interval}, or lose that
 * much on a crash. Each station seen since startup stays in memory until SteVe stops.
 *
 * @see <a href="https://github.com/steve-community/steve/issues/2164">Issue 2164</a>
 */
@Slf4j
@Service
public class ChargeBoxActivityService {

    private static final Duration MIN_SAVE_INTERVAL = Duration.ofSeconds(1);

    private final OcppServerRepository ocppServerRepository;
    private final TaskScheduler taskScheduler;
    private final Duration saveInterval;

    private final Map<String, ChargeBoxActivity> activityByChargeBoxId = new ConcurrentHashMap<>();
    private final Set<String> unsavedChargeBoxIds = ConcurrentHashMap.newKeySet();

    private ScheduledFuture<?> scheduledSaving;

    public ChargeBoxActivityService(OcppServerRepository ocppServerRepository,
                                    TaskScheduler taskScheduler,
                                    SteveProperties steveProperties) {
        var saveInterval = steveProperties.getOcpp().getActivitySaveInterval();
        if (saveInterval == null || saveInterval.compareTo(MIN_SAVE_INTERVAL) < 0) {
            throw new IllegalArgumentException(
                "Activity save interval must be at least " + MIN_SAVE_INTERVAL + ", but was " + saveInterval);
        }

        this.ocppServerRepository = ocppServerRepository;
        this.taskScheduler = taskScheduler;
        this.saveInterval = saveInterval;
    }

    @PostConstruct
    void startSaving() {
        scheduledSaving = taskScheduler.scheduleWithFixedDelay(
            this::saveAll,
            Instant.now().plus(saveInterval),
            saveInterval
        );
    }

    @PreDestroy
    void stopSaving() {
        scheduledSaving.cancel(false);
        saveAll();
    }

    public void ocppMessageReceived(String chargeBoxId) {
        update(chargeBoxId, activity -> activity.withLastOcppMessage(DateTime.now()));
    }

    public void pongReceived(String chargeBoxId) {
        update(chargeBoxId, activity -> activity.withLastPong(DateTime.now()));
    }

    @Nullable
    public ChargeBoxActivity getActivity(String chargeBoxId) {
        return activityByChargeBoxId.get(chargeBoxId);
    }

    /**
     * The activity of the given stations: the timestamps in memory, completed with the saved ones for what was not
     * received since SteVe started tracking a station.
     */
    public Map<String, ChargeBoxActivity> getLatestActivity(Collection<String> chargeBoxIds) {
        if (chargeBoxIds.isEmpty()) {
            return Map.of();
        }

        var saved = ocppServerRepository.getChargeboxActivity(chargeBoxIds);
        Map<String, ChargeBoxActivity> latest = new HashMap<>();
        for (var chargeBoxId : chargeBoxIds) {
            var inMemory = activityByChargeBoxId.getOrDefault(chargeBoxId, ChargeBoxActivity.NONE);
            latest.put(chargeBoxId, inMemory.withDefaultsFrom(saved.get(chargeBoxId)));
        }
        return latest;
    }

    // synchronized, so that stopSaving() waits for a periodic save that is still running
    synchronized void saveAll() {
        Map<String, ChargeBoxActivity> toSave = new HashMap<>();
        var it = unsavedChargeBoxIds.iterator();
        while (it.hasNext()) {
            var chargeBoxId = it.next();
            it.remove();
            toSave.put(chargeBoxId, activityByChargeBoxId.get(chargeBoxId));
        }
        if (toSave.isEmpty()) {
            return;
        }

        try {
            ocppServerRepository.updateChargeboxActivity(toSave);
        } catch (Exception e) {
            log.error("Failed to save the activity of {} station(s), kept for the next attempt", toSave.size(), e);
            unsavedChargeBoxIds.addAll(toSave.keySet());
        }
    }

    private void update(String chargeBoxId, UnaryOperator<ChargeBoxActivity> change) {
        activityByChargeBoxId.compute(
            chargeBoxId,
            (_, activity) -> change.apply(activity == null ? ChargeBoxActivity.NONE : activity)
        );
        unsavedChargeBoxIds.add(chargeBoxId);
    }
}

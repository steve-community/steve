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
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ChargeBoxActivityServiceTest {

    private OcppServerRepository repository;
    private ChargeBoxActivityService service;

    @BeforeEach
    public void setup() {
        repository = mock(OcppServerRepository.class);
        service = new ChargeBoxActivityService(repository, mock(TaskScheduler.class), new SteveProperties());
    }

    @AfterEach
    public void resetClock() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void rejectsASaveIntervalBelowOneSecond() {
        var properties = new SteveProperties();
        properties.getOcpp().setActivitySaveInterval(Duration.ZERO);

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new ChargeBoxActivityService(repository, mock(TaskScheduler.class), properties)
        );
    }

    @Test
    public void rejectsAnEmptySaveInterval() {
        var properties = new SteveProperties();
        properties.getOcpp().setActivitySaveInterval(null);

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new ChargeBoxActivityService(repository, mock(TaskScheduler.class), properties)
        );
    }

    @Test
    public void acceptsASaveIntervalOfOneSecond() {
        var properties = new SteveProperties();
        properties.getOcpp().setActivitySaveInterval(Duration.ofSeconds(1));

        Assertions.assertDoesNotThrow(
            () -> new ChargeBoxActivityService(repository, mock(TaskScheduler.class), properties)
        );
    }

    @Test
    public void keepsEachTimestampInMemoryWithoutSaving() {
        var first = new DateTime(2026, 1, 1, 0, 0);
        var second = first.plusMinutes(1);
        var third = second.plusMinutes(1);

        DateTimeUtils.setCurrentMillisFixed(first.getMillis());
        service.ocppMessageReceived("cb1");
        Assertions.assertEquals(new ChargeBoxActivity(first, null), service.getActivity("cb1"));

        DateTimeUtils.setCurrentMillisFixed(second.getMillis());
        service.pongReceived("cb1");
        Assertions.assertEquals(new ChargeBoxActivity(first, second), service.getActivity("cb1"));

        DateTimeUtils.setCurrentMillisFixed(third.getMillis());
        service.ocppMessageReceived("cb1");
        Assertions.assertEquals(new ChargeBoxActivity(third, second), service.getActivity("cb1"));

        verify(repository, never()).updateChargeboxActivity(any());
    }

    @Test
    public void saveAllSavesOnlyWhatChangedSinceTheLastSave() {
        service.ocppMessageReceived("cb1");
        service.ocppMessageReceived("cb2");

        service.saveAll();
        Assertions.assertEquals(
            Map.of("cb1", service.getActivity("cb1"), "cb2", service.getActivity("cb2")),
            lastSaved()
        );

        service.ocppMessageReceived("cb2");
        service.saveAll();
        Assertions.assertEquals(Map.of("cb2", service.getActivity("cb2")), lastSaved());

        service.saveAll();
        verify(repository, times(2)).updateChargeboxActivity(any());
    }

    @Test
    public void failedSaveIsRetriedWithTheNextSave() {
        service.ocppMessageReceived("cb1");
        doThrow(new RuntimeException("database down")).when(repository).updateChargeboxActivity(any());
        service.saveAll();

        reset(repository);
        service.saveAll();

        Assertions.assertEquals(Map.of("cb1", service.getActivity("cb1")), lastSaved());
    }

    @Test
    public void latestActivityOfNoStationsDoesNotQueryTheDatabase() {
        Assertions.assertEquals(Map.of(), service.getLatestActivity(List.of()));
        verifyNoInteractions(repository);
    }

    @Test
    public void latestActivityTakesFromTheDatabaseWhatIsNotInMemory() {
        var saved = new DateTime(2026, 1, 1, 0, 0);
        when(repository.getChargeboxActivity(List.of("cb1", "cb2", "cb3"))).thenReturn(Map.of(
            "cb1", new ChargeBoxActivity(saved, saved),
            "cb2", new ChargeBoxActivity(saved, null)
        ));
        service.pongReceived("cb1");
        service.ocppMessageReceived("cb3");

        var latest = service.getLatestActivity(List.of("cb1", "cb2", "cb3"));

        Assertions.assertEquals(new ChargeBoxActivity(saved, service.getActivity("cb1").lastPong()), latest.get("cb1"));
        Assertions.assertEquals(new ChargeBoxActivity(saved, null), latest.get("cb2"));
        Assertions.assertEquals(service.getActivity("cb3"), latest.get("cb3"));
    }

    @Test
    public void savesEveryIntervalStartingOneIntervalFromNow() {
        var taskScheduler = mock(TaskScheduler.class);
        var interval = new SteveProperties().getOcpp().getActivitySaveInterval();
        service = new ChargeBoxActivityService(repository, taskScheduler, new SteveProperties());
        ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> startTime = ArgumentCaptor.forClass(Instant.class);

        var before = Instant.now();
        service.startSaving();
        var after = Instant.now();

        verify(taskScheduler).scheduleWithFixedDelay(task.capture(), startTime.capture(), eq(interval));
        Assertions.assertFalse(startTime.getValue().isBefore(before.plus(interval)));
        Assertions.assertFalse(startTime.getValue().isAfter(after.plus(interval)));

        service.ocppMessageReceived("cb1");
        task.getValue().run();
        Assertions.assertEquals(Map.of("cb1", service.getActivity("cb1")), lastSaved());
    }

    @Test
    public void stopSavingCancelsTheScheduleAndSavesWhatIsLeft() {
        var taskScheduler = mock(TaskScheduler.class);
        ScheduledFuture<?> scheduledSaving = mock(ScheduledFuture.class);
        when(taskScheduler.scheduleWithFixedDelay(any(Runnable.class), any(Instant.class), any(Duration.class)))
            .thenAnswer(_ -> scheduledSaving);
        service = new ChargeBoxActivityService(repository, taskScheduler, new SteveProperties());

        service.startSaving();
        service.ocppMessageReceived("cb1");
        service.stopSaving();

        verify(scheduledSaving).cancel(false);
        Assertions.assertEquals(Map.of("cb1", service.getActivity("cb1")), lastSaved());
    }

    @SuppressWarnings("unchecked")
    private Map<String, ChargeBoxActivity> lastSaved() {
        ArgumentCaptor<Map<String, ChargeBoxActivity>> captor = ArgumentCaptor.forClass(Map.class);
        verify(repository, atLeastOnce()).updateChargeboxActivity(captor.capture());
        return captor.getValue();
    }
}

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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.TaskOverview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Current implementation does double bookkeeping of tasks by numeric and UUID.
 * Deprecate numeric taskId bookkeeping later and base all operations on {@link CommunicationTask#taskUuid}.
 * Afterwards, we can delete modificationLock and synchronized blocks.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Slf4j
@Repository
public class TaskStoreImpl implements TaskStore {

    private final Object modificationLock = new Object();
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, CommunicationTask> lookupTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> taskIdByUuid = new ConcurrentHashMap<>();

    @Override
    public List<TaskOverview> getOverview() {
        return lookupTable.entrySet()
                          .stream()
                          .map(entry -> {
                              CommunicationTask r = entry.getValue();
                              return TaskOverview.builder()
                                                 .taskId(entry.getKey())
                                                 .origin(r.getOrigin())
                                                 .start(r.getStartTimestamp())
                                                 .end(r.getEndTimestamp())
                                                 .responseCount(r.getResponseCount().get())
                                                 .requestCount(r.getResultMap().size())
                                                 .build();
                          })
                          .sorted()
                          .collect(Collectors.toList());
    }

    @Override
    public CommunicationTask get(Integer taskId) {
        CommunicationTask r = lookupTable.get(taskId);
        if (r == null) {
            throw new SteveException("There is no task with taskId '%s'", taskId);
        } else {
            return r;
        }
    }

    @Override
    public CommunicationTask get(UUID taskUuid) {
        Integer taskId = taskIdByUuid.get(taskUuid);
        if (taskId == null) {
            throw new SteveException("There is no task with taskUuid '%s'", taskUuid);
        }
        return get(taskId);
    }

    @Override
    public Integer add(CommunicationTask task) {
        synchronized (modificationLock) {
            int taskId = atomicInteger.incrementAndGet();
            var existingTask = lookupTable.putIfAbsent(taskId, task);
            if (existingTask != null) {
                throw new SteveException("There is already a task with taskId '%s'", taskId);
            }

            var existingTaskId = taskIdByUuid.putIfAbsent(task.getTaskUuid(), taskId);
            if (existingTaskId != null) {
                lookupTable.remove(taskId, task);
                throw new SteveException("There is already a task with taskUuid '%s'", task.getTaskUuid());
            }

            return taskId;
        }
    }

    @Override
    public boolean remove(Integer taskId, CommunicationTask task) {
        synchronized (modificationLock) {
            if (!lookupTable.remove(taskId, task)) {
                return false;
            }
            taskIdByUuid.remove(task.getTaskUuid(), taskId);
            return true;
        }
    }

    @Override
    public List<CommunicationTask> getFinished() {
        return lookupTable.values()
            .stream()
            .filter(CommunicationTask::isFinished)
            .toList();
    }

    @Override
    public List<CommunicationTask> getUnfinished() {
        return lookupTable.values()
            .stream()
            .filter(task -> !task.isFinished())
            .toList();
    }

    @Override
    public void clearFinished() {
        synchronized (modificationLock) {
            removeTasks(entry -> entry.getValue().isFinished());
        }
    }

    @Override
    public void clearUnfinished() {
        synchronized (modificationLock) {
            removeTasks(entry -> !entry.getValue().isFinished());
        }
    }

    private void removeTasks(Predicate<Map.Entry<Integer, CommunicationTask>> filterPredicate) {
        lookupTable.entrySet()
                   .stream()
                   .filter(filterPredicate)
                   .forEach(entry -> remove(entry.getKey(), entry.getValue()));
    }
}

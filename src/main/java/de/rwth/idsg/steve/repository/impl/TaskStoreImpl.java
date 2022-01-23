/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Slf4j
@Repository
public class TaskStoreImpl implements TaskStore {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, CommunicationTask> lookupTable = new ConcurrentHashMap<>();

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
    public Integer add(CommunicationTask task) {
        int taskId = atomicInteger.incrementAndGet();
        lookupTable.put(taskId, task);
        return taskId;
    }

    @Override
    public void clearFinished() {
        lookupTable.entrySet()
                   .stream()
                   .filter(entry -> entry.getValue().isFinished())
                   .forEach(entry -> lookupTable.remove(entry.getKey()));
    }
}

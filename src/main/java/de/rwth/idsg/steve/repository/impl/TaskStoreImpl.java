/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.ws.cluster.ClusteredWebSocketConfig;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.TaskOverview;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.PersistentTask;
import net.parkl.ocpp.service.cluster.PersistentTaskConverter;
import net.parkl.ocpp.service.cluster.PersistentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.AsyncHandler;
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
    @Autowired
    private ClusteredWebSocketConfig clusteredWebSocketConfig;
    @Autowired
    private PersistentTaskService persistentTaskService;

    @Autowired
    private PersistentTaskConverter persistentTaskConverter;

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
        if (r == null && clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            r = loadPersistentTask(taskId);
        }
        if (r == null) {
            throw new SteveException("There is no task with taskId '%s'", taskId);
        } else {
            return r;
        }
    }

    private CommunicationTask loadPersistentTask(Integer taskId) {
        log.info("Loading persistent task: {}", taskId);
        PersistentTask persistentTask = persistentTaskService.findById(taskId);
        return persistentTaskConverter.fromPersistentTask(persistentTask);
    }

    @Override
    public Integer add(CommunicationTask task) {
        int taskId;
        if (clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            taskId = savePersistentTask(task);
        } else {
            taskId = atomicInteger.incrementAndGet();
        }
        task.setTaskId(taskId);
        lookupTable.put(taskId, task);
        return taskId;
    }

    private int savePersistentTask(CommunicationTask task) {
        PersistentTask persistentTask = persistentTaskConverter.toPersistentTask(task);
        PersistentTask saved = persistentTaskService.save(persistentTask);
        return saved.getPersistentTaskId();
    }

    @Override
    public void clearFinished() {
        lookupTable.entrySet()
                   .stream()
                   .filter(entry -> entry.getValue().isFinished())
                   .forEach(entry -> lookupTable.remove(entry.getKey()));
    }
}

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
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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

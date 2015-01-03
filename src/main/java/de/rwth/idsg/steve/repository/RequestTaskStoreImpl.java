package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.TaskOverview;
import de.rwth.idsg.steve.web.RequestTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Slf4j
@Repository
public class RequestTaskStoreImpl implements RequestTaskStore {

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, RequestTask> lookupTable = new ConcurrentHashMap<>();

    @Override
    public List<TaskOverview> getOverview() {
        List<TaskOverview> list = new ArrayList<>();
        for (Map.Entry<Integer, RequestTask> entry : lookupTable.entrySet()) {
            RequestTask r = entry.getValue();
            list.add(
                    TaskOverview.builder()
                            .taskId(entry.getKey())
                            .start(r.getStartTimestamp())
                            .end(r.getEndTimestamp())
                            .responseCount(r.getResponseCount().get())
                            .requestCount(r.getResultMap().size())
                            .build()
            );
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public RequestTask get(Integer taskId) {
        RequestTask r = lookupTable.get(taskId);
        if (r == null) {
            throw new SteveException(String.format("There is no task with taskId '%s'", taskId));
        } else {
            return r;
        }
    }

    @Override
    public Integer add(RequestTask task) {
        int taskId = atomicInteger.incrementAndGet();
        lookupTable.put(taskId, task);
        return taskId;
    }

    @Override
    public void clearFinished() {
        for (Map.Entry<Integer, RequestTask> entry : lookupTable.entrySet()) {
            if (entry.getValue().isFinished()) {
                lookupTable.remove(entry.getKey());
            }
        }
    }
}

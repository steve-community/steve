package net.parkl.ocpp.service.cluster;

import de.rwth.idsg.steve.ocpp.ws.cluster.ClusteredWebSocketConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.PersistentTask;
import net.parkl.ocpp.entities.PersistentTaskResult;
import net.parkl.ocpp.repositories.PersistentTaskRepository;
import net.parkl.ocpp.repositories.PersistentTaskResultRepository;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersistentTaskServiceImpl implements PersistentTaskService {
    private final PersistentTaskRepository taskRepository;
    private final PersistentTaskResultRepository taskResultRepository;
    private final ClusteredWebSocketConfig clusteredWebSocketConfig;

    public PersistentTask findById(Integer taskId) {
        return taskRepository.findById(taskId).orElseThrow(()->new IllegalArgumentException("Invalid task id: "+taskId));
    }

    @Transactional
    public PersistentTask save(PersistentTask persistentTask) {
        return taskRepository.save(persistentTask);
    }

    @Override
    @Transactional
    public void addNewResponse(int taskId, String chargeBoxId, String response, DateTime endTimestamp) {
        if (clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled() && taskId !=0) {
            log.info("Adding new persistent response: {}-{}, {}", taskId, chargeBoxId, response);
            PersistentTask task = findById(taskId);

            PersistentTaskResult result = new PersistentTaskResult();
            result.setTask(task);
            result.setChargeBoxId(chargeBoxId);
            result.setResponse(response);
            taskResultRepository.save(result);

            if (endTimestamp!=null) {
                task.setEndTimestamp(endTimestamp);
                taskRepository.save(task);
            }
        }
    }

    @Override
    @Transactional
    public void addNewError(int taskId, String chargeBoxId, String errorMessage, DateTime endTimestamp) {
        if (clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled() && taskId != 0) {
            log.info("Adding new persistent error: {}-{}, {}", taskId, chargeBoxId, errorMessage);

            PersistentTask task = findById(taskId);

            PersistentTaskResult result = new PersistentTaskResult();
            result.setTask(task);
            result.setChargeBoxId(chargeBoxId);
            result.setErrorMessage(errorMessage);
            taskResultRepository.save(result);

            if (endTimestamp != null) {
                task.setEndTimestamp(endTimestamp);
                taskRepository.save(task);
            }

        }

    }

    public List<PersistentTaskResult> findResultsByTask(PersistentTask persistentTask) {
        return taskResultRepository.findByTask(persistentTask);
    }
}

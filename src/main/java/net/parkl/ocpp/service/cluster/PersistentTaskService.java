package net.parkl.ocpp.service.cluster;

import net.parkl.ocpp.entities.PersistentTask;
import net.parkl.ocpp.entities.PersistentTaskResult;

import java.util.List;

public interface PersistentTaskService extends PersistentTaskResultCallback{
    PersistentTask findById(Integer taskId);

    PersistentTask save(PersistentTask persistentTask);

    List<PersistentTaskResult> findResultsByTask(PersistentTask persistentTask);
}

package net.parkl.ocpp.service.cluster;

import lombok.RequiredArgsConstructor;
import net.parkl.ocpp.entities.PersistentTask;
import net.parkl.ocpp.repositories.PersistentTaskRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistentTaskService {
    private final PersistentTaskRepository taskRepository;

    public PersistentTask findById(Integer taskId) {
        return taskRepository.findById(taskId).orElseThrow(()->new IllegalArgumentException("Invalid task id: "+taskId));
    }
}

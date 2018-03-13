package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.TaskOverview;
import de.rwth.idsg.steve.web.dto.task.RequestTask;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
public interface RequestTaskStore {
    List<TaskOverview> getOverview();
    RequestTask get(Integer taskId);
    Integer add(RequestTask task);
    void clearFinished();
}

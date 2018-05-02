package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.repository.dto.TaskOverview;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
public interface TaskStore {
    List<TaskOverview> getOverview();
    CommunicationTask get(Integer taskId);
    Integer add(CommunicationTask task);
    void clearFinished();
}

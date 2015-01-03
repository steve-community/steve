package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.experimental.Builder;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@Builder
public class TaskOverview implements Comparable<TaskOverview> {
    private int taskId, responseCount, requestCount;
    private DateTime start, end;

    /**
     * We want the tasks to be printed in descending order.
     */
    @Override
    public int compareTo(TaskOverview o) {
        return (o.taskId - this.taskId);
    }
}

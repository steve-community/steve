/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.TaskOrigin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Getter
@EqualsAndHashCode
@Builder
public final class TaskOverview implements Comparable<TaskOverview> {
    @ApiModelProperty(value = "Task ID")
    private final int taskId;
    @ApiModelProperty(value = "Response count")
    private final int responseCount;
    @ApiModelProperty(value = "Request count")
    private final int requestCount;
    @ApiModelProperty(value = "Starttime")
    private final DateTime start;
    @ApiModelProperty(value = "Endtime")
    private final DateTime end;
    @ApiModelProperty(value = "Task triggered internal or external")
    private final TaskOrigin origin;

    /**
     * We want the tasks to be printed in descending order.
     */
    @Override
    public int compareTo(TaskOverview o) {
        return (o.taskId - this.taskId);
    }
}

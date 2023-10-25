/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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
package de.rwth.idsg.steve.web.api.dto;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.ocpp.TaskOrigin;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */

@Getter
@Setter
public class ApiTaskInfo {
    @ApiModelProperty(value = "Task ID")
    public Integer taskId;
    @ApiModelProperty(value = "OCPP version")
    public OcppVersion ocppVersion;
    @ApiModelProperty(value = "OCPP operation")
    public String operationName;
    @ApiModelProperty(value = "external / internal")
    public TaskOrigin origin;
    @ApiModelProperty(value = "Caller of the Task")
    public String caller;

    @ApiModelProperty(value = "Results")
    public Map<String, RequestResult> resultMap;
    @ApiModelProperty(value = "Count of Results")
    public int resultSize;

    @ApiModelProperty(value = "Starttime")
    public DateTime startTimestamp = DateTime.now();
    @ApiModelProperty(value = "Endtime")
    public DateTime endTimestamp;

    @ApiModelProperty(value = "Error count")
    public AtomicInteger errorCount = new AtomicInteger(0);
    @ApiModelProperty(value = "Response count")
    public AtomicInteger responseCount = new AtomicInteger(0);


    public ApiTaskInfo(Integer taskId, CommunicationTask r) {
        this.taskId = taskId;
        this.ocppVersion = r.getOcppVersion();
        this.operationName = r.getOperationName();
        this.origin = r.getOrigin();
        this.caller = r.getCaller();


        this.resultMap = r.getResultMap();
        this.resultSize = r.getResultSize();

        this.startTimestamp = r.getStartTimestamp();
        this.endTimestamp = r.getEndTimestamp();

        this.errorCount = r.getErrorCount();
        this.responseCount = r.getResponseCount();
    }

}

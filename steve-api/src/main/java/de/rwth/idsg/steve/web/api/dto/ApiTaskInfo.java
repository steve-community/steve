/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */
@Getter
@Setter
public class ApiTaskInfo {
    @Schema(description = "Task ID")
    private Integer taskId;

    @Schema(description = "OCPP version")
    private OcppVersion ocppVersion;

    @Schema(description = "OCPP operation")
    private String operationName;

    @Schema(description = "external / internal")
    private TaskOrigin origin;

    @Schema(description = "Caller of the Task")
    private String caller;

    @Schema(description = "Results")
    private Map<String, ? extends RequestResult<?>> resultMap;

    @Schema(description = "Count of Results")
    private int resultSize;

    @Schema(description = "Starttime")
    private OffsetDateTime startTimestamp;

    @Schema(description = "Endtime")
    private OffsetDateTime endTimestamp;

    @Schema(description = "Error count")
    private int errorCount;

    @Schema(description = "Response count")
    private int responseCount;

    public ApiTaskInfo(Integer taskId, CommunicationTask<?, ?> r) {
        this.taskId = taskId;
        this.operationName = r.getOperationName();
        this.origin = r.getOrigin();
        this.caller = r.getCaller();

        this.resultMap = r.getResultMap();
        this.resultSize = r.getResultSize();

        this.startTimestamp = r.getStartTimestamp();
        this.endTimestamp = r.getEndTimestamp();

        this.errorCount = r.getErrorCount().get();
        this.responseCount = r.getResponseCount().get();
    }
}

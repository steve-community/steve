/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkongsheng
 * @since 19.10.2026
 */
@Tag(name = "task-controller",
    description = """
        Operations for retrieving the status and results of asynchronous OCPP operation tasks.
        Every OCPP operation response contains a taskId. Tasks are held in memory and remain
        queryable until they are cleared via the manager UI. Unknown or already-cleared task
        ids return 404.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TasksRestController {

    private final TaskStore taskStore;

    @Operation(description = """
        Returns the status and results of an asynchronous OCPP operation task by its taskId.
        Partial and final responses are distinguishable via taskFinished. Success responses
        and error responses are reported separately.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found - the task id is unknown or the task was already cleared", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping("/{taskId}")
    public TaskResponse getOne(@PathVariable("taskId") Integer taskId) {
        log.debug("Read request for taskId: {}", taskId);

        CommunicationTask task;
        try {
            task = taskStore.get(taskId);
        } catch (SteveException e) {
            // The store throws when the task is unknown. Expose it as 404, as documented
            // in the OpenAPI description of this endpoint.
            throw new SteveException.NotFound(
                "Could not find this task. Note that tasks are held in memory only and are removed when cleared via the manager UI.");
        }

        var response = TaskResponse.of(taskId, task);
        log.debug("Read response: {}", response);
        return response;
    }
}

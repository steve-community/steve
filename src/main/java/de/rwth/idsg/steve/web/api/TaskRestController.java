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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.repository.TaskStore;

import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.api.dto.ApiTaskInfo;
import de.rwth.idsg.steve.web.api.dto.ApiTaskList;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskRestController {

    @Autowired private TaskStore taskStore;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ApiTaskList getTaskList() {
        ApiTaskList taskList = new ApiTaskList();
        taskList.setTasks(taskStore.getOverview());
        return taskList;
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

   @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "taskoverview")
    @ResponseBody
    public ApiTaskList getOverview() {
        return getTaskList();
    }


    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping(value = "clearfinishedtasks")
    @ResponseBody
    public ApiTaskList clearFinished() {
        taskStore.clearFinished();
        return getTaskList();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "task")
    @ResponseBody
    public ApiTaskInfo getTaskDetails(@RequestParam(name="id") @Valid Integer taskId) {
        ApiTaskInfo taskInfo = new ApiTaskInfo(taskId, taskStore.get(taskId));
        return taskInfo;
    }
}

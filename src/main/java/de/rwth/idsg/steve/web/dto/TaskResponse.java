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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Schema(description = "Status and results of an asynchronous OCPP operation task")
public record TaskResponse(
    int taskId,
    String operation,
    @Schema(description = """
        RUNNING: the task is still waiting for (some) stations to complete communication.
        COMPLETED: all stations responded with a CallResult.
        COMPLETED_WITH_ERRORS: the task finished, but at least one station responded with a CallError or failed.
        FAILED: all stations failed.
        """)
    String state,
    DateTime createdAt,
    DateTime completedAt,
    @Schema(description = """
        True when all stations have completed communication (either with a CallResult, CallError, or exception)
        before the platform timeout. If false, only partial results are available.
        """)
    boolean taskFinished,
    int responseCount,
    int errorCount,
    int resultSize,
    List<StationResult> successResponses,
    List<StationResult> errorResponses
) {

    public record StationResult(String chargeBoxId, String response) {
    }

    public static TaskResponse of(int taskId, CommunicationTask task) {
        List<StationResult> successResponses = new java.util.ArrayList<>();
        List<StationResult> errorResponses = new java.util.ArrayList<>();

        // Read counters once and copy the result map, so a concurrent station callback
        // cannot make one response contradict itself.
        boolean finished = task.getEndTimestamp() != null;
        int responseCount = task.getResponseCount().get();
        int errorCount = task.getErrorCount().get();

        Map<String, RequestResult> resultMap = new TreeMap<>(task.getResultMap());
        resultMap.forEach((chargeBoxId, result) -> {
            if (result.getResponse() != null) {
                successResponses.add(new StationResult(chargeBoxId, result.getResponse()));
            } else if (result.getErrorMessage() != null) {
                errorResponses.add(new StationResult(chargeBoxId, result.getErrorMessage()));
            } else if (finished) {
                // A station whose callback threw a message-less exception is counted as an
                // error but has no stored message; report it instead of dropping it silently.
                errorResponses.add(new StationResult(chargeBoxId, "The operation failed without an error message"));
            }
        });

        String state;
        if (!finished) {
            state = "RUNNING";
        } else if (errorCount == resultMap.size()) {
            state = "FAILED";
        } else if (errorCount > 0) {
            state = "COMPLETED_WITH_ERRORS";
        } else {
            state = "COMPLETED";
        }

        return new TaskResponse(
            taskId,
            task.getOperationName(),
            state,
            task.getStartTimestamp(),
            task.getEndTimestamp(),
            finished,
            responseCount,
            errorCount,
            resultMap.size(),
            successResponses,
            errorResponses
        );
    }
}

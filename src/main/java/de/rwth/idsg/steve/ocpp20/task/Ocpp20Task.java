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
package de.rwth.idsg.steve.ocpp20.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public abstract class Ocpp20Task<REQUEST, RESPONSE> {

    protected final ObjectMapper objectMapper = createObjectMapper();
    protected final String operationName;
    protected final List<String> chargeBoxIds;

    private final Map<String, TaskResult> resultMap = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<RESPONSE>> pendingRequests = new ConcurrentHashMap<>();

    private final DateTime startTimestamp = DateTime.now();
    private DateTime endTimestamp;

    protected Ocpp20Task(String operationName, List<String> chargeBoxIds) {
        this.operationName = operationName;
        this.chargeBoxIds = chargeBoxIds;

        for (String chargeBoxId : chargeBoxIds) {
            resultMap.put(chargeBoxId, new TaskResult());
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    public abstract REQUEST createRequest();

    public Class<RESPONSE> getResponseClass() {
        throw new UnsupportedOperationException("Must override getResponseClass()");
    }

    public void addResult(String chargeBoxId, RESPONSE response) {
        TaskResult result = resultMap.get(chargeBoxId);
        if (result != null) {
            try {
                result.setResponse(objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                log.error("Error serializing response", e);
                result.setErrorMessage(e.getMessage());
            }
        }
        checkCompletion();
    }

    public void addError(String chargeBoxId, String errorCode, String errorDescription) {
        TaskResult result = resultMap.get(chargeBoxId);
        if (result != null) {
            result.setErrorMessage(String.format("[%s] %s", errorCode, errorDescription));
        }
        checkCompletion();
    }

    public CompletableFuture<RESPONSE> getPendingRequest(String messageId) {
        return pendingRequests.get(messageId);
    }

    public void registerPendingRequest(String messageId, CompletableFuture<RESPONSE> future) {
        pendingRequests.put(messageId, future);
    }

    public void removePendingRequest(String messageId) {
        pendingRequests.remove(messageId);
    }

    private void checkCompletion() {
        if (resultMap.values().stream().allMatch(TaskResult::isCompleted)) {
            endTimestamp = DateTime.now();
        }
    }

    public boolean isFinished() {
        return endTimestamp != null;
    }

    public Map<String, String> getResults() {
        Map<String, String> results = new HashMap<>();
        for (Map.Entry<String, TaskResult> entry : resultMap.entrySet()) {
            TaskResult result = entry.getValue();
            if (result.getResponse() != null) {
                results.put(entry.getKey(), result.getResponse());
            } else if (result.getErrorMessage() != null) {
                results.put(entry.getKey(), "ERROR: " + result.getErrorMessage());
            } else {
                results.put(entry.getKey(), "PENDING");
            }
        }
        return results;
    }

    @Getter
    public static class TaskResult {
        private String response;
        private String errorMessage;

        public void setResponse(String response) {
            this.response = response;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public boolean isCompleted() {
            return response != null || errorMessage != null;
        }
    }
}
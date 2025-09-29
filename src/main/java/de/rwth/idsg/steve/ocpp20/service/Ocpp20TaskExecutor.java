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
package de.rwth.idsg.steve.ocpp20.service;

import de.rwth.idsg.steve.ocpp20.task.Ocpp20Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20TaskExecutor {

    private final Ocpp20MessageDispatcher messageDispatcher;
    private final Executor taskExecutor = Executors.newFixedThreadPool(10);
    private final Map<String, TaskStatus> taskStatuses = new ConcurrentHashMap<>();

    public <REQ, RES> void execute(Ocpp20Task<REQ, RES> task) {
        String taskId = task.getTaskId();
        taskStatuses.put(taskId, new TaskStatus(taskId, "RUNNING"));

        log.info("Executing OCPP 2.0 task: {} for {} charge points", task.getAction(), task.getChargeBoxIds().size());

        for (String chargeBoxId : task.getChargeBoxIds()) {
            taskExecutor.execute(() -> executeForChargePoint(task, chargeBoxId));
        }
    }

    private <REQ, RES> void executeForChargePoint(Ocpp20Task<REQ, RES> task, String chargeBoxId) {
        try {
            REQ request = task.createRequest();
            CompletableFuture<RES> future = messageDispatcher.sendCall(
                chargeBoxId,
                task.getAction(),
                request,
                task.getResponseClass()
            );

            future.whenComplete((response, throwable) -> {
                if (throwable != null) {
                    log.error("Task '{}' failed for charge point '{}': {}",
                        task.getAction(), chargeBoxId, throwable.getMessage());
                } else {
                    log.info("Task '{}' completed for charge point '{}': {}",
                        task.getAction(), chargeBoxId, response);
                }
            });

        } catch (Exception e) {
            log.error("Error executing task '{}' for charge point '{}'",
                task.getAction(), chargeBoxId, e);
        }
    }

    public TaskStatus getTaskStatus(String taskId) {
        return taskStatuses.get(taskId);
    }

    public static class TaskStatus {
        private final String taskId;
        private final String status;

        public TaskStatus(String taskId, String status) {
            this.taskId = taskId;
            this.status = status;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getStatus() {
            return status;
        }
    }
}
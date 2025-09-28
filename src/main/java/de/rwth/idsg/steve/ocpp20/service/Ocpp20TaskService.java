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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20TaskService {

    private final Ocpp20MessageDispatcher dispatcher;

    public <REQ, RES> Map<String, String> executeTask(Ocpp20Task<REQ, RES> task) {
        Map<String, String> results = new HashMap<>();
        List<String> chargeBoxIds = task.getChargeBoxIds();

        log.info("Executing OCPP 2.0 task '{}' for {} charge box(es)",
            task.getOperationName(), chargeBoxIds.size());

        Map<String, CompletableFuture<RES>> futures = new HashMap<>();

        for (String chargeBoxId : chargeBoxIds) {
            if (!dispatcher.hasActiveSession(chargeBoxId)) {
                results.put(chargeBoxId, "ERROR: No active WebSocket session");
                log.warn("Charge box '{}' has no active OCPP 2.0 session", chargeBoxId);
                continue;
            }

            try {
                REQ request = task.createRequest();
                CompletableFuture<RES> future = dispatcher.sendCall(
                    chargeBoxId,
                    task.getOperationName(),
                    request,
                    task.getResponseClass()
                );
                futures.put(chargeBoxId, future);

            } catch (Exception e) {
                results.put(chargeBoxId, "ERROR: " + e.getMessage());
                log.error("Error creating request for charge box '{}'", chargeBoxId, e);
            }
        }

        for (Map.Entry<String, CompletableFuture<RES>> entry : futures.entrySet()) {
            String chargeBoxId = entry.getKey();
            CompletableFuture<RES> future = entry.getValue();

            try {
                RES response = future.get(35, TimeUnit.SECONDS);
                results.put(chargeBoxId, "SUCCESS: " + response.toString());
                log.info("Task '{}' completed successfully for '{}'",
                    task.getOperationName(), chargeBoxId);

            } catch (java.util.concurrent.TimeoutException e) {
                results.put(chargeBoxId, "ERROR: Timeout waiting for response");
                log.error("Timeout executing task '{}' for '{}'",
                    task.getOperationName(), chargeBoxId);

            } catch (Exception e) {
                String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                results.put(chargeBoxId, "ERROR: " + errorMsg);
                log.error("Error executing task '{}' for '{}'",
                    task.getOperationName(), chargeBoxId, e);
            }
        }

        log.info("Task '{}' execution completed. Success: {}, Failed: {}",
            task.getOperationName(),
            results.values().stream().filter(v -> v.startsWith("SUCCESS")).count(),
            results.values().stream().filter(v -> v.startsWith("ERROR")).count());

        return results;
    }
}
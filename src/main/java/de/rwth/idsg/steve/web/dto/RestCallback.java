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

import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
@Slf4j
public class RestCallback<T> implements OcppCallback<T> {

    private final Duration timeoutDuration;
    private final CountDownLatch countDownLatch;

    private final Map<String, T> successResponsesByChargeBoxId = new ConcurrentHashMap<>();
    private final Map<String, OcppJsonError> errorResponsesByChargeBoxId = new ConcurrentHashMap<>();
    private final Map<String, Exception> exceptionsByChargeBoxId = new ConcurrentHashMap<>();

    @Setter
    private int taskId;

    private boolean finished = false;

    @Override
    public void success(String chargeBoxId, T response) {
        successResponsesByChargeBoxId.put(chargeBoxId, response);
        countDownLatch.countDown();
    }

    @Override
    public void success(String chargeBoxId, OcppJsonError error) {
        errorResponsesByChargeBoxId.put(chargeBoxId, error);
        countDownLatch.countDown();
    }

    @Override
    public void failed(String chargeBoxId, Exception e) {
        exceptionsByChargeBoxId.put(chargeBoxId, e);
        countDownLatch.countDown();
    }

    /**
     * Wait for responses to arrive from all stations
     */
    public void waitForResponses() throws Exception {
        long seconds = timeoutDuration.getSeconds();
        finished = countDownLatch.await(seconds, TimeUnit.SECONDS);
        if (!finished) {
            log.warn("Some stations did not respond within {} seconds", seconds);
        }
    }
}

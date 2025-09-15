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
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.task.impl.RequestMapper;
import de.rwth.idsg.steve.ocpp.task.impl.ResponseMapper;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.utils.StringUtils;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.xml.ws.AsyncHandler;

/**
 * Contains the context for a request/response communication and callbacks for handling responses/errors.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
@Getter
public abstract class CommunicationTask<S extends ChargePointSelection, RESPONSE> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String operationName;
    private final TaskOrigin origin;
    private final String caller;
    protected final S params;
    protected final TaskDefinition<S, RESPONSE> taskDefinition;

    private final Map<String, OcppVersion> versionMap;
    private final Map<String, RequestResult<RESPONSE>> resultMap;
    private final int resultSize;

    private final Instant startTimestamp = Instant.now();
    private @Nullable Instant endTimestamp;

    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicInteger responseCount = new AtomicInteger(0);

    @Getter(AccessLevel.NONE) // disable getter generation
    private final Object lockObject = new Object();

    private final List<OcppCallback<RESPONSE>> callbackList;

    @Getter
    private final OcppCallback<RESPONSE> defaultCallback;

    protected CommunicationTask(TaskDefinition<S, RESPONSE> taskDefinition, S params) {
        this(taskDefinition, params, TaskOrigin.INTERNAL, "SteVe");
    }

    protected CommunicationTask(TaskDefinition<S, RESPONSE> taskDefinition, S params, String caller) {
        this(taskDefinition, params, TaskOrigin.EXTERNAL, caller);
    }

    private CommunicationTask(TaskDefinition<S, RESPONSE> taskDefinition, S params, TaskOrigin origin, String caller) {
        var cpsList = params.getChargePointSelectList();

        this.resultSize = cpsList.size();
        this.origin = origin;
        this.caller = caller;
        this.params = params;
        this.taskDefinition = taskDefinition;

        this.resultMap = HashMap.newHashMap(resultSize);
        this.versionMap = HashMap.newHashMap(resultSize);
        for (var cps : cpsList) {
            resultMap.put(cps.getChargeBoxId(), new RequestResult<>());
            versionMap.put(cps.getChargeBoxId(), cps.getOcppProtocol().getVersion());
        }

        this.callbackList = new ArrayList<>(2);

        this.defaultCallback = createDefaultCallback();
        callbackList.add(this.defaultCallback);

        this.operationName = StringUtils.getOperationName(this);
    }

    public void addCallback(OcppCallback<RESPONSE> cb) {
        callbackList.add(cb);
    }

    public boolean isFinished() {
        synchronized (lockObject) {
            return endTimestamp != null;
        }
    }

    public void addNewResponse(String chargeBoxId, RESPONSE response) {
        var result = resultMap.get(chargeBoxId);
        if (result == null) {
            log.warn("Received response for unknown chargeBoxId '{}'", chargeBoxId);
            return;
        }
        result.setResponse(response);

        synchronized (lockObject) {
            if (resultSize == (errorCount.get() + responseCount.incrementAndGet())) {
                endTimestamp = Instant.now();
            }
        }
    }

    public void addNewError(String chargeBoxId, @Nullable String errorMessage) {
        resultMap.get(chargeBoxId).setErrorMessage(errorMessage);

        synchronized (lockObject) {
            if (resultSize == (errorCount.incrementAndGet() + responseCount.get())) {
                endTimestamp = Instant.now();
            }
        }
    }

    public void success(String chargeBoxId, RESPONSE response) {
        for (var c : callbackList) {
            try {
                c.success(chargeBoxId, response);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }

    public void failed(String chargeBoxId, Exception exception) {
        for (var c : callbackList) {
            try {
                c.failed(chargeBoxId, exception);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }

    protected OcppCallback<RESPONSE> createDefaultCallback() {
        return new OcppCallback<>() {
            @Override
            public void success(String chargeBoxId, RESPONSE response) {
                addNewResponse(chargeBoxId, response);
            }

            @Override
            public void successError(String chargeBoxId, Object error) {
                addNewError(chargeBoxId, error.toString());
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends RequestType> T getRequest(OcppVersion version) {
        var handler = taskDefinition.getVersionHandlers().get(version);
        if (handler == null) {
            throw new UnsupportedOperationException("Operation not supported for version " + version);
        }
        var mapper = (RequestMapper<CommunicationTask<S, RESPONSE>, T>) handler.getRequestMapper();
        return mapper.map(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResponseType> AsyncHandler<T> createHandler(String chargeBoxId) {
        var version = versionMap.get(chargeBoxId);
        var handler = taskDefinition.getVersionHandlers().get(version);
        if (handler == null) {
            throw new UnsupportedOperationException("Operation not supported for version " + version);
        }
        var mapper = (ResponseMapper<T, RESPONSE>) handler.getResponseMapper();

        return res -> {
            try {
                var mappedResponse = mapper.map(res.get());
                success(chargeBoxId, mappedResponse);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}

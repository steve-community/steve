/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.utils.StringUtils;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import lombok.AccessLevel;
import lombok.Getter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.AsyncHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contains the context for a request/response communication and callbacks for handling responses/errors.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
@Getter
public abstract class CommunicationTask<S extends ChargePointSelection, RESPONSE> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final OcppVersion ocppVersion;
    private final String operationName;
    private final TaskOrigin origin;
    private final String caller;
    protected final S params;

    private final Map<String, RequestResult> resultMap;
    private final int resultSize;

    private final DateTime startTimestamp = DateTime.now();
    private DateTime endTimestamp;

    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicInteger responseCount = new AtomicInteger(0);

    @Getter(AccessLevel.NONE) // disable getter generation
    private final Object lockObject = new Object();

    // The default initial capacity is 10. We probably won't need that much.
    private final ArrayList<OcppCallback<RESPONSE>> callbackList = new ArrayList<>(2);

    public CommunicationTask(OcppVersion ocppVersion, S params) {
        this(ocppVersion, params, TaskOrigin.INTERNAL, "SteVe");
    }

    /**
     * Do not expose the constructor, make it package-private
     */
    CommunicationTask(OcppVersion ocppVersion, S params, TaskOrigin origin, String caller) {
        List<ChargePointSelect> cpsList = params.getChargePointSelectList();

        this.ocppVersion = ocppVersion;
        this.resultSize = cpsList.size();
        this.origin = origin;
        this.caller = caller;
        this.params = params;

        resultMap = new HashMap<>(resultSize);
        for (ChargePointSelect cps : cpsList) {
            resultMap.put(cps.getChargeBoxId(), new RequestResult());
        }

        callbackList.add(defaultCallback());
        operationName = StringUtils.getOperationName(this);
    }

    public void addCallback(OcppCallback<RESPONSE> cb) {
        callbackList.add(cb);
    }

    public boolean isFinished() {
        synchronized (lockObject) {
            return endTimestamp != null;
        }
    }

    public void addNewResponse(String chargeBoxId, String response) {
        resultMap.get(chargeBoxId).setResponse(response);

        synchronized (lockObject) {
            if (resultSize == (errorCount.get() + responseCount.incrementAndGet())) {
                endTimestamp = DateTime.now();
            }
        }
    }

    public void addNewError(String chargeBoxId, String errorMessage) {
        resultMap.get(chargeBoxId).setErrorMessage(errorMessage);

        synchronized (lockObject) {
            if (resultSize == (errorCount.incrementAndGet() + responseCount.get())) {
                endTimestamp = DateTime.now();
            }
        }
    }

    protected void success(String chargeBoxId, RESPONSE response) {
        for (OcppCallback<RESPONSE> c : callbackList) {
            try {
                c.success(chargeBoxId, response);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }

    protected void failed(String chargeBoxId, Exception exception) {
        for (OcppCallback<RESPONSE> c : callbackList) {
            try {
                c.failed(chargeBoxId, exception);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }

    public RequestType getRequest() {
        switch (ocppVersion) {
            case V_12: return getOcpp12Request();
            case V_15: return getOcpp15Request();
            case V_16: return getOcpp16Request();
            default: throw new RuntimeException("Request type not found");
        }
    }

    public <T extends ResponseType> AsyncHandler<T> getHandler(String chargeBoxId) {
        switch (ocppVersion) {
            case V_12: return getOcpp12Handler(chargeBoxId);
            case V_15: return getOcpp15Handler(chargeBoxId);
            case V_16: return getOcpp16Handler(chargeBoxId);
            default: throw new RuntimeException("ResponseType handler not found");
        }
    }

    public abstract OcppCallback<RESPONSE> defaultCallback();

    public abstract <T extends RequestType> T getOcpp12Request();
    public abstract <T extends RequestType> T getOcpp15Request();
    public abstract <T extends RequestType> T getOcpp16Request();

    public abstract <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId);
    public abstract <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId);
    public abstract <T extends ResponseType> AsyncHandler<T> getOcpp16Handler(String chargeBoxId);

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------

    public abstract class DefaultOcppCallback<RES> implements OcppCallback<RES> {

        public abstract void success(String chargeBoxId, RES response);

        @Override
        public void success(String chargeBoxId, OcppJsonError error) {
            addNewResponse(chargeBoxId, error.toString());
        }

        @Override
        public void failed(String chargeBoxId, Exception e) {
            addNewError(chargeBoxId, e.getMessage());
        }
    }

    public class StringOcppCallback extends DefaultOcppCallback<String> {

        @Override
        public void success(String chargeBoxId, String response) {
            addNewResponse(chargeBoxId, response);
        }
    }
}

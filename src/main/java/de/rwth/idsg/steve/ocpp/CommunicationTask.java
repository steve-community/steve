package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import de.rwth.idsg.steve.web.dto.task.RequestResult;
import de.rwth.idsg.steve.web.dto.task.RequestTaskOrigin;
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
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
@Getter
public abstract class CommunicationTask<S extends ChargePointSelection, RESPONSE> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final OcppVersion ocppVersion;
    private final String operationName;
    private final RequestTaskOrigin origin;
    private final String caller;
    protected final S params;

    private final Map<String, RequestResult> resultMap;
    private final int resultSize;

    private final DateTime startTimestamp = DateTime.now();
    private DateTime endTimestamp;

    private AtomicInteger errorCount = new AtomicInteger(0);
    private AtomicInteger responseCount = new AtomicInteger(0);

    @Getter(AccessLevel.NONE) // disable getter generation
    private final Object lockObject = new Object();

    // The default initial capacity is 10. We probably won't need that much.
    private ArrayList<OcppCallback<RESPONSE>> callbackList = new ArrayList<>(2);

    public CommunicationTask(OcppVersion ocppVersion, S params) {
        this(ocppVersion, params, RequestTaskOrigin.INTERNAL, "SteVe");
    }

    /**
     * Do not expose the constructor, make it package-private
     */
    CommunicationTask(OcppVersion ocppVersion, S params, RequestTaskOrigin origin, String caller) {
        List<ChargePointSelect> cpsList = params.getChargePointSelectList();

        this.operationName = ""; // TODO replace this: StringUtils.getOperationName(requestType);
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

    public abstract OcppCallback<RESPONSE> defaultCallback();

    public abstract <T extends RequestType> T getOcpp12Request();

    public abstract <T extends RequestType> T getOcpp15Request();

    public abstract <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId);

    public abstract <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId);
}

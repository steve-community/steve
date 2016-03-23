package de.rwth.idsg.steve.web.dto.task;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contains information about asynchronous request tasks (OCPP requests and responses/errors)
 * for displaying on Web UI.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.09.2014
 */
@Getter
public class RequestTask<S extends RequestType> {
    private final OcppVersion ocppVersion;
    private final String operationName;
    private final RequestTaskOrigin origin;
    private final String caller;
    private final S request;

    private final Map<String, RequestResult> resultMap;
    private final int resultSize;

    private final DateTime startTimestamp = DateTime.now();
    private DateTime endTimestamp;

    private AtomicInteger errorCount = new AtomicInteger(0);
    private AtomicInteger responseCount = new AtomicInteger(0);

    @Getter(AccessLevel.NONE) // disable getter generation
    private final Object lockObject = new Object();

    public RequestTask(OcppVersion ocppVersion, S requestType, List<ChargePointSelect> cpsList) {
        this(ocppVersion, requestType, cpsList, RequestTaskOrigin.INTERNAL, "SteVe");
    }

    /**
     * Do not expose the constructor, make it package-private
     */
    RequestTask(OcppVersion ocppVersion, S requestType, List<ChargePointSelect> cpsList,
                RequestTaskOrigin origin, String caller) {

        this.operationName = StringUtils.getOperationName(requestType);
        this.ocppVersion = ocppVersion;
        this.resultSize = cpsList.size();
        this.origin = origin;
        this.caller = caller;
        this.request = requestType;

        resultMap = new HashMap<>(resultSize);
        for (ChargePointSelect cps : cpsList) {
            resultMap.put(cps.getChargeBoxId(), new RequestResult());
        }
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

    public void addNewError(String chargeBoxId, Exception exception) {
        resultMap.get(chargeBoxId).setErrorMessage(exception.getMessage());

        synchronized (lockObject) {
            if (resultSize == (errorCount.incrementAndGet() + responseCount.get())) {
                endTimestamp = DateTime.now();
            }
        }
    }
}

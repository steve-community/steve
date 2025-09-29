package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class GetLogTask extends Ocpp20Task<GetLogRequest, GetLogResponse> {

    private final Integer requestId;
    private final LogParameters log;
    private final LogEnum logType;
    private final Integer retries;
    private final Integer retryInterval;

    public GetLogTask(List<String> chargeBoxIdList, Integer requestId, String remoteLocation, LogEnum logType) {
        this(chargeBoxIdList, requestId, createLogParameters(remoteLocation), logType, null, null);
    }

    public GetLogTask(List<String> chargeBoxIdList, Integer requestId, LogParameters log, LogEnum logType, Integer retries, Integer retryInterval) {
        super("GetLog", chargeBoxIdList);
        this.requestId = requestId;
        this.log = log;
        this.logType = logType;
        this.retries = retries;
        this.retryInterval = retryInterval;
    }

    private static LogParameters createLogParameters(String remoteLocation) {
        LogParameters params = new LogParameters();
        params.setRemoteLocation(remoteLocation);
        return params;
    }

    @Override
    public GetLogRequest createRequest() {
        GetLogRequest request = new GetLogRequest();
        request.setRequestId(requestId);
        request.setLog(log);
        request.setLogType(logType);

        if (retries != null) {
            request.setRetries(retries);
        }

        if (retryInterval != null) {
            request.setRetryInterval(retryInterval);
        }

        return request;
    }

    @Override
    public Class<GetLogResponse> getResponseClass() {
        return GetLogResponse.class;
    }
}

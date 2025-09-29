package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.PublishFirmwareRequest;
import de.rwth.idsg.steve.ocpp20.model.PublishFirmwareResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class PublishFirmwareTask extends Ocpp20Task<PublishFirmwareRequest, PublishFirmwareResponse> {

    private final String location;
    private final Integer retries;
    private final Integer retryInterval;
    private final String checksum;
    private final Integer requestId;

    public PublishFirmwareTask(List<String> chargeBoxIdList, String location, Integer retries,
                               Integer retryInterval, String checksum, Integer requestId) {
        super("PublishFirmware", chargeBoxIdList);
        this.location = location;
        this.retries = retries;
        this.retryInterval = retryInterval;
        this.checksum = checksum;
        this.requestId = requestId;
    }

    @Override
    public PublishFirmwareRequest createRequest() {
        PublishFirmwareRequest request = new PublishFirmwareRequest();
        request.setLocation(location);
        request.setRetries(retries);
        request.setRetryInterval(retryInterval);
        request.setChecksum(checksum);
        request.setRequestId(requestId);
        return request;
    }

    @Override
    public Class<PublishFirmwareResponse> getResponseClass() {
        return PublishFirmwareResponse.class;
    }
}

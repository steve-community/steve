package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class UpdateFirmwareTask extends Ocpp20Task<UpdateFirmwareRequest, UpdateFirmwareResponse> {

    private final Integer requestId;
    private final Firmware firmware;
    private final Integer retries;
    private final Integer retryInterval;

    public UpdateFirmwareTask(List<String> chargeBoxIdList, Integer requestId, String location) {
        this(chargeBoxIdList, requestId, createFirmware(location), null, null);
    }

    public UpdateFirmwareTask(List<String> chargeBoxIdList, Integer requestId, Firmware firmware, Integer retries, Integer retryInterval) {
        super("UpdateFirmware", chargeBoxIdList);
        this.requestId = requestId;
        this.firmware = firmware;
        this.retries = retries;
        this.retryInterval = retryInterval;
    }

    private static Firmware createFirmware(String location) {
        Firmware fw = new Firmware();
        fw.setLocation(location);
        fw.setRetrieveDateTime(OffsetDateTime.now());
        return fw;
    }

    @Override
    public UpdateFirmwareRequest createRequest() {
        UpdateFirmwareRequest request = new UpdateFirmwareRequest();
        request.setRequestId(requestId);
        request.setFirmware(firmware);

        if (retries != null) {
            request.setRetries(retries);
        }

        if (retryInterval != null) {
            request.setRetryInterval(retryInterval);
        }

        return request;
    }

    @Override
    public Class<UpdateFirmwareResponse> getResponseClass() {
        return UpdateFirmwareResponse.class;
    }
}

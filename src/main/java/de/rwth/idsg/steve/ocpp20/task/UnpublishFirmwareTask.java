package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.UnpublishFirmwareRequest;
import de.rwth.idsg.steve.ocpp20.model.UnpublishFirmwareResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class UnpublishFirmwareTask extends Ocpp20Task<UnpublishFirmwareRequest, UnpublishFirmwareResponse> {

    private final String checksum;

    public UnpublishFirmwareTask(List<String> chargeBoxIdList, String checksum) {
        super("UnpublishFirmware", chargeBoxIdList);
        this.checksum = checksum;
    }

    @Override
    public UnpublishFirmwareRequest createRequest() {
        UnpublishFirmwareRequest request = new UnpublishFirmwareRequest();
        request.setChecksum(checksum);
        return request;
    }

    @Override
    public Class<UnpublishFirmwareResponse> getResponseClass() {
        return UnpublishFirmwareResponse.class;
    }
}

package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.util.List;

/**
 * OCPP 2.0 SetNetworkProfile task implementation
 */
@Getter
public class SetNetworkProfileTask extends Ocpp20Task<SetNetworkProfileRequest, SetNetworkProfileResponse> {

    private final Integer configurationSlot;
    private final NetworkConnectionProfile connectionData;

    public SetNetworkProfileTask(List<String> chargeBoxIds, Integer configurationSlot,
                                 NetworkConnectionProfile connectionData) {
        super("SetNetworkProfile", chargeBoxIds);
        this.configurationSlot = configurationSlot;
        this.connectionData = connectionData;
    }

    @Override
    public SetNetworkProfileRequest createRequest() {
        SetNetworkProfileRequest request = new SetNetworkProfileRequest();
        request.setConfigurationSlot(configurationSlot);
        request.setConnectionData(connectionData);
        return request;
    }

    @Override
    public Class<SetNetworkProfileResponse> getResponseClass() {
        return SetNetworkProfileResponse.class;
    }
}
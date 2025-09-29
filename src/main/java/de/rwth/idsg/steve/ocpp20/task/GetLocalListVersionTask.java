package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetLocalListVersionRequest;
import de.rwth.idsg.steve.ocpp20.model.GetLocalListVersionResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class GetLocalListVersionTask extends Ocpp20Task<GetLocalListVersionRequest, GetLocalListVersionResponse> {

    public GetLocalListVersionTask(List<String> chargeBoxIdList) {
        super("GetLocalListVersion", chargeBoxIdList);
    }

    @Override
    public GetLocalListVersionRequest createRequest() {
        return new GetLocalListVersionRequest();
    }

    @Override
    public Class<GetLocalListVersionResponse> getResponseClass() {
        return GetLocalListVersionResponse.class;
    }
}
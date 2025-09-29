package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;

import java.util.List;

@Getter
public class GetChargingProfilesTask extends Ocpp20Task<GetChargingProfilesRequest, GetChargingProfilesResponse> {

    private final Integer requestId;
    private final Integer evseId;
    private final ChargingProfileCriterion chargingProfileCriterion;

    public GetChargingProfilesTask(List<String> chargeBoxIdList, Integer requestId) {
        this(chargeBoxIdList, requestId, null, null);
    }

    public GetChargingProfilesTask(List<String> chargeBoxIdList, Integer requestId, Integer evseId) {
        this(chargeBoxIdList, requestId, evseId, null);
    }

    public GetChargingProfilesTask(List<String> chargeBoxIdList, Integer requestId, Integer evseId, ChargingProfileCriterion criterion) {
        super("GetChargingProfiles", chargeBoxIdList);
        this.requestId = requestId;
        this.evseId = evseId;
        this.chargingProfileCriterion = criterion;
    }

    @Override
    public GetChargingProfilesRequest createRequest() {
        GetChargingProfilesRequest request = new GetChargingProfilesRequest();
        request.setRequestId(requestId);

        if (evseId != null) {
            request.setEvseId(evseId);
        }

        if (chargingProfileCriterion != null) {
            request.setChargingProfile(chargingProfileCriterion);
        } else {
            ChargingProfileCriterion defaultCriterion = new ChargingProfileCriterion();
            request.setChargingProfile(defaultCriterion);
        }

        return request;
    }

    @Override
    public Class<GetChargingProfilesResponse> getResponseClass() {
        return GetChargingProfilesResponse.class;
    }
}
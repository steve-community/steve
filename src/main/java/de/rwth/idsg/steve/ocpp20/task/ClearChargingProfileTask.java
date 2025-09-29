package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.util.List;

@Getter
public class ClearChargingProfileTask extends Ocpp20Task<ClearChargingProfileRequest, ClearChargingProfileResponse> {

    private final Integer chargingProfileId;
    private final ClearChargingProfile clearChargingProfileCriteria;

    public ClearChargingProfileTask(List<String> chargeBoxIdList) {
        this(chargeBoxIdList, null, null);
    }

    public ClearChargingProfileTask(List<String> chargeBoxIdList, Integer chargingProfileId) {
        this(chargeBoxIdList, chargingProfileId, null);
    }

    public ClearChargingProfileTask(List<String> chargeBoxIdList, Integer chargingProfileId, ClearChargingProfile criteria) {
        super("ClearChargingProfile", chargeBoxIdList);
        this.chargingProfileId = chargingProfileId;
        this.clearChargingProfileCriteria = criteria;
    }

    @Override
    public ClearChargingProfileRequest createRequest() {
        ClearChargingProfileRequest request = new ClearChargingProfileRequest();

        if (chargingProfileId != null) {
            request.setChargingProfileId(chargingProfileId);
        }

        if (clearChargingProfileCriteria != null) {
            request.setChargingProfileCriteria(clearChargingProfileCriteria);
        }

        return request;
    }

    @Override
    public Class<ClearChargingProfileResponse> getResponseClass() {
        return ClearChargingProfileResponse.class;
    }
}

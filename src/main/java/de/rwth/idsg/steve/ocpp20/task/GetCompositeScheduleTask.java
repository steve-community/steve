package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.util.List;

@Getter
public class GetCompositeScheduleTask extends Ocpp20Task<GetCompositeScheduleRequest, GetCompositeScheduleResponse> {

    private final Integer duration;
    private final Integer evseId;
    private final ChargingRateUnitEnum chargingRateUnit;

    public GetCompositeScheduleTask(List<String> chargeBoxIdList, Integer duration, Integer evseId) {
        this(chargeBoxIdList, duration, evseId, null);
    }

    public GetCompositeScheduleTask(List<String> chargeBoxIdList, Integer duration, Integer evseId, ChargingRateUnitEnum chargingRateUnit) {
        super("GetCompositeSchedule", chargeBoxIdList);
        this.duration = duration;
        this.evseId = evseId;
        this.chargingRateUnit = chargingRateUnit;
    }

    @Override
    public GetCompositeScheduleRequest createRequest() {
        GetCompositeScheduleRequest request = new GetCompositeScheduleRequest();
        request.setDuration(duration);
        request.setEvseId(evseId);

        if (chargingRateUnit != null) {
            request.setChargingRateUnit(chargingRateUnit);
        }

        return request;
    }

    @Override
    public Class<GetCompositeScheduleResponse> getResponseClass() {
        return GetCompositeScheduleResponse.class;
    }
}

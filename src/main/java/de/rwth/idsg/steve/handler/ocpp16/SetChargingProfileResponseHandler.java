package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.SetChargingProfileRequest;
import ocpp.cp._2015._10.SetChargingProfileResponse;

public class SetChargingProfileResponseHandler
        extends AbstractOcppResponseHandler<SetChargingProfileRequest, SetChargingProfileResponse>
{
    public SetChargingProfileResponseHandler(RequestTask<SetChargingProfileRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(SetChargingProfileResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}

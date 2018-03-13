package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.ClearChargingProfileRequest;
import ocpp.cp._2015._10.ClearChargingProfileResponse;


public class ClearChargingProfileResponseHandler extends AbstractOcppResponseHandler<ClearChargingProfileRequest, ClearChargingProfileResponse>
{
    public ClearChargingProfileResponseHandler(RequestTask<ClearChargingProfileRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(ClearChargingProfileResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
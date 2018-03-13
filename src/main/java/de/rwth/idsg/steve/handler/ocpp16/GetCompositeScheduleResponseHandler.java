package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.GetCompositeScheduleRequest;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;

public class GetCompositeScheduleResponseHandler extends AbstractOcppResponseHandler<GetCompositeScheduleRequest, GetCompositeScheduleResponse>
{
    public GetCompositeScheduleResponseHandler(RequestTask<GetCompositeScheduleRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(GetCompositeScheduleResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}

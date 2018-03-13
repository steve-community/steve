package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.TriggerMessageRequest;
import ocpp.cp._2015._10.TriggerMessageResponse;


public class TriggerMessageResponseHandler extends AbstractOcppResponseHandler<TriggerMessageRequest, TriggerMessageResponse>
{
    public TriggerMessageResponseHandler(RequestTask<TriggerMessageRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(TriggerMessageResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
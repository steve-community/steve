package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.ResetResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class ResetResponseHandler extends AbstractOcppResponseHandler<ResetRequest, ResetResponse> {

    public ResetResponseHandler(ResetRequest req, RequestTask task, String chargeBoxId) {
        super(req, task, chargeBoxId);
    }

    @Override
    public void handleResult(ResetResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
